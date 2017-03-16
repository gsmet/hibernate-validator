/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.metadata.aggregated;

import static org.hibernate.validator.internal.util.CollectionHelper.newHashSet;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ElementKind;

import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.stereotypes.Immutable;

/**
 * Base implementation for {@link ConstraintMetaData} with attributes common
 * to all type of meta data.
 *
 * @author Gunnar Morling
 * @author Hardy Ferentschik
 */
public abstract class AbstractConstraintMetaData implements ConstraintMetaData {
	private final String name;
	private final Type type;
	private final ElementKind constrainedMetaDataKind;
	@Immutable
	private final Set<MetaConstraint<?>> constraints;
	private final boolean isCascading;
	private final boolean isConstrained;

	public AbstractConstraintMetaData(String name,
									  Type type,
									  Set<MetaConstraint<?>> constraints,
									  ElementKind constrainedMetaDataKind,
									  boolean isCascading,
									  boolean isConstrained) {
		this.name = name;
		this.type = type;
		this.constraints = CollectionHelper.toImmutableSet( constraints );
		this.constrainedMetaDataKind = constrainedMetaDataKind;
		this.isCascading = isCascading;
		this.isConstrained = isConstrained;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Iterator<MetaConstraint<?>> iterator() {
		return constraints.iterator();
	}

	public Set<MetaConstraint<?>> getConstraints() {
		return constraints;
	}

	@Override
	public ElementKind getKind() {
		return constrainedMetaDataKind;
	}

	@Override
	public final boolean isCascading() {
		return isCascading;
	}

	@Override
	public boolean isConstrained() {
		return isConstrained;
	}

	@Override
	public String toString() {
		return "AbstractConstraintMetaData [name=" + name + ", type=" + type
				+ ", constrainedMetaDataKind=" + constrainedMetaDataKind
				+ ", constraints=" + constraints + ", isCascading="
				+ isCascading + ", isConstrained=" + isConstrained + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		AbstractConstraintMetaData other = (AbstractConstraintMetaData) obj;
		if ( name == null ) {
			if ( other.name != null ) {
				return false;
			}
		}
		else if ( !name.equals( other.name ) ) {
			return false;
		}
		return true;
	}

	protected Set<ConstraintDescriptorImpl<?>> asDescriptors(Set<MetaConstraint<?>> constraints) {
		Set<ConstraintDescriptorImpl<?>> theValue = newHashSet();

		for ( MetaConstraint<?> oneConstraint : constraints ) {
			theValue.add( oneConstraint.getDescriptor() );
		}

		return theValue;
	}
}
