/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ElementKind;
import javax.validation.metadata.GroupConversionDescriptor;
import javax.validation.metadata.ParameterDescriptor;

import org.hibernate.validator.internal.engine.cascading.ValueExtractors;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.engine.valuehandling.UnwrapMode;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ParameterDescriptorImpl;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement.ConstrainedElementKind;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;

/**
 * An aggregated view of the constraint related meta data for a single method
 * parameter.
 *
 * @author Gunnar Morling
 * @author Hardy Ferentschik
 */
public class ParameterMetaData extends AbstractConstraintMetaData implements Cascadable {

	private final GroupConversionHelper groupConversionHelper;
	private final int index;
	private final List<TypeVariable<?>> cascadingTypeParameters;

	private ParameterMetaData(int index,
							  String name,
							  Type type,
							  Set<MetaConstraint<?>> constraints,
							  List<TypeVariable<?>> cascadingTypeParameters,
							  Map<Class<?>, Class<?>> groupConversions,
							  UnwrapMode unwrapMode) {
		super(
				name,
				type,
				constraints,
				ElementKind.PARAMETER,
				!cascadingTypeParameters.isEmpty(),
				!constraints.isEmpty() || !cascadingTypeParameters.isEmpty(),
				unwrapMode
		);

		this.index = index;

		this.cascadingTypeParameters = Collections.unmodifiableList( cascadingTypeParameters );
		this.groupConversionHelper = new GroupConversionHelper( groupConversions );
		this.groupConversionHelper.validateGroupConversions( isCascading(), this.toString() );
	}

	public int getIndex() {
		return index;
	}

	@Override
	public Class<?> convertGroup(Class<?> originalGroup) {
		return groupConversionHelper.convertGroup( originalGroup );
	}

	@Override
	public Set<GroupConversionDescriptor> getGroupConversionDescriptors() {
		return groupConversionHelper.asDescriptors();
	}

	@Override
	public ElementType getElementType() {
		return ElementType.PARAMETER;
	}

	@Override
	public ParameterDescriptor asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
		return new ParameterDescriptorImpl(
				getType(),
				index,
				getName(),
				asDescriptors( getConstraints() ),
				isCascading(),
				defaultGroupSequenceRedefined,
				defaultGroupSequence,
				getGroupConversionDescriptors()
		);
	}

	@Override
	public Object getValue(Object parent) {
		return ( (Object[]) parent )[getIndex()];
	}

	@Override
	public Type getCascadableType() {
		return getType();
	}

	@Override
	public void appendTo(PathImpl path) {
		path.addParameterNode( getName(), getIndex() );
	}

	@Override
	public List<TypeVariable<?>> getCascadingTypeParameters() {
		return cascadingTypeParameters;
	}

	public static class Builder extends MetaDataBuilder {
		private final Type parameterType;
		private final int parameterIndex;
		private ConstrainedParameter constrainedParameter;
		private final List<TypeVariable<?>> cascadingTypeParameters = new ArrayList<>();

		public Builder(Class<?> beanClass,
				ConstrainedParameter constrainedParameter,
				ConstraintHelper constraintHelper,
				TypeResolutionHelper typeResolutionHelper,
				ValueExtractors valueExtractors) {
			super( beanClass, constraintHelper, typeResolutionHelper, valueExtractors );

			this.parameterType = constrainedParameter.getType();
			this.parameterIndex = constrainedParameter.getIndex();

			add( constrainedParameter );
		}

		@Override
		public boolean accepts(ConstrainedElement constrainedElement) {
			if ( constrainedElement.getKind() != ConstrainedElementKind.PARAMETER ) {
				return false;
			}

			return ( (ConstrainedParameter) constrainedElement ).getIndex() == parameterIndex;
		}

		@Override
		public void add(ConstrainedElement constrainedElement) {
			super.add( constrainedElement );

			ConstrainedParameter newConstrainedParameter = (ConstrainedParameter) constrainedElement;

			cascadingTypeParameters.addAll( newConstrainedParameter.getCascadingTypeParameters() );

			if ( constrainedParameter == null ) {
				constrainedParameter = newConstrainedParameter;
			}
			else if ( newConstrainedParameter.getExecutable().getDeclaringClass().isAssignableFrom(
					constrainedParameter.getExecutable().getDeclaringClass()
			) ) {
				// If the current parameter is from a method hosted on a parent class,
				// use this parent class parameter name instead of the more specific one.
				// Worse case, we are consistent, best case parameters from parents are more meaningful.
				// See HV-887 and the associated unit test
				constrainedParameter = newConstrainedParameter;
			}
		}

		@Override
		public ParameterMetaData build() {
			return new ParameterMetaData(
					parameterIndex,
					constrainedParameter.getName(),
					parameterType,
					adaptOriginsAndImplicitGroups( getConstraints() ),
					cascadingTypeParameters,
					getGroupConversions(),
					unwrapMode()
			);
		}
	}
}
