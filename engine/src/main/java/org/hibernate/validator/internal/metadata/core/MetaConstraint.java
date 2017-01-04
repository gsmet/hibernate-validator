/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.metadata.core;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.Set;

import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.engine.cascading.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.spi.cascading.ValueExtractor;

/**
 * Instances of this class abstract the constraint type  (class, method or field constraint) and give access to
 * meta data about the constraint. This allows a unified handling of constraints in the validator implementation.
 *
 * @author Hardy Ferentschik
 * @author Gunnar Morling
 * @author Guillaume Smet
 */
public class MetaConstraint<A extends Annotation> {

	/**
	 * The constraint tree created from the constraint annotation.
	 */
	private final ConstraintTree<A> constraintTree;

	/**
	 * The constraint descriptor.
	 */
	private final ConstraintDescriptorImpl<A> constraintDescriptor;

	/**
	 * The location at which this constraint is defined.
	 */
	private final ConstraintLocation location;

	/**
	 * The {@link ValueExtractor} used to extract the value for validation.
	 */
	private final ValueExtractorDescriptor valueExtractorDescriptor;

	/**
	 * The type of the validated element.
	 */
	private final Type typeOfValidatedElement;

	/**
	 * @param constraintDescriptor The constraint descriptor for this constraint
	 * @param location meta data about constraint placement
	 * @param valueExtractorDescriptor the potential {@link ValueExtractor} used to extract the value to validate
	 * @param typeOfValidatedElement the type of the validated element
	 */
	MetaConstraint(ConstraintDescriptorImpl<A> constraintDescriptor, ConstraintLocation location, ValueExtractorDescriptor valueExtractorDescriptor,
			Type typeOfValidatedElement) {
		this.constraintTree = new ConstraintTree<>( constraintDescriptor );
		this.constraintDescriptor = constraintDescriptor;
		this.location = location;
		this.valueExtractorDescriptor = valueExtractorDescriptor;
		this.typeOfValidatedElement = typeOfValidatedElement;
	}

	/**
	 * @return Returns the list of groups this constraint is part of. This might include the default group even when
	 *         it is not explicitly specified, but part of the redefined default group list of the hosting bean.
	 */
	public final Set<Class<?>> getGroupList() {
		return constraintDescriptor.getGroups();
	}

	public final ConstraintDescriptorImpl<A> getDescriptor() {
		return constraintDescriptor;
	}

	public final ElementType getElementType() {
		return constraintDescriptor.getElementType();
	}

	public boolean validateConstraint(ValidationContext<?> executionContext, ValueContext<?, ?> valueContext) {
		valueContext.setElementType( getElementType() );
		boolean validationResult = constraintTree.validateConstraints( executionContext, valueContext );

		return validationResult;
	}

	public ConstraintLocation getLocation() {
		return location;
	}

	public ValueExtractorDescriptor getValueExtractorDescriptor() {
		return valueExtractorDescriptor;
	}

	public Type getTypeOfValidatedElement() {
		return typeOfValidatedElement;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		MetaConstraint<?> that = (MetaConstraint<?>) o;

		if ( constraintDescriptor != null ? !constraintDescriptor.equals( that.constraintDescriptor ) : that.constraintDescriptor != null ) {
			return false;
		}
		if ( location != null ? !location.equals( that.location ) : that.location != null ) {
			return false;
		}
		if ( valueExtractorDescriptor != null ? !valueExtractorDescriptor.equals( that.valueExtractorDescriptor ) : that.valueExtractorDescriptor != null ) {
			return false;
		}
		if ( typeOfValidatedElement != null ? !typeOfValidatedElement.equals( that.typeOfValidatedElement ) : that.typeOfValidatedElement != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = constraintDescriptor != null ? constraintDescriptor.hashCode() : 0;
		result = 31 * result + ( location != null ? location.hashCode() : 0 );
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append( "MetaConstraint" );
		sb.append( "{constraintType=" ).append( constraintDescriptor.getAnnotation().annotationType().getName() );
		sb.append( ", location=" ).append( location );
		sb.append( ", valueExtractorDescriptor=" ).append( valueExtractorDescriptor );
		sb.append( ", typeOfValidatedElement=" ).append( typeOfValidatedElement );
		sb.append( "}" );
		return sb.toString();
	}
}
