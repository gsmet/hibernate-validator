/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.cfg.context;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.CrossParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl.ConstraintType;

/**
 * Constraint mapping creational context which allows to configure the constraints for one method return value.
 *
 * @author Hardy Ferentschik
 * @author Gunnar Morling
 * @author Kevin Pollet &lt;kevin.pollet@serli.com&gt; (C) 2011 SERLI
 */
final class ReturnValueConstraintMappingContextImpl
		extends CascadableConstraintMappingContextImplBase<ReturnValueConstraintMappingContext>
		implements ReturnValueConstraintMappingContext {

	private final ExecutableConstraintMappingContextImpl executableContext;

	ReturnValueConstraintMappingContextImpl(ExecutableConstraintMappingContextImpl executableContext) {
		super( executableContext.getTypeContext().getConstraintMapping() );
		this.executableContext = executableContext;
	}

	@Override
	protected ReturnValueConstraintMappingContext getThis() {
		return this;
	}

	@Override
	public ReturnValueConstraintMappingContext constraint(ConstraintDef<?, ?> definition) {
		super.addConstraint( ConfiguredConstraint.forReturnValue( definition, executableContext.getExecutable() ) );
		return this;
	}

	@Override
	public ReturnValueConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
		mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsForReturnValue(
				executableContext.getExecutable().getMember(), ignoreAnnotations
		);
		return this;
	}

	@Override
	public ParameterConstraintMappingContext parameter(int index) {
		return executableContext.parameter( index );
	}

	@Override
	public CrossParameterConstraintMappingContext crossParameter() {
		return executableContext.crossParameter();
	}

	@Override
	public MethodConstraintMappingContext method(String name, Class<?>... parameterTypes) {
		return executableContext.getTypeContext().method( name, parameterTypes );
	}

	@Override
	public ConstructorConstraintMappingContext constructor(Class<?>... parameterTypes) {
		return executableContext.getTypeContext().constructor( parameterTypes );
	}

	@Override
	protected ConstraintType getConstraintType() {
		return ConstraintType.GENERIC;
	}
}
