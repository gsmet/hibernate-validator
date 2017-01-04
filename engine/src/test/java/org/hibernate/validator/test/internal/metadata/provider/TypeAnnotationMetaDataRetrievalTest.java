/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.test.internal.metadata.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.internal.engine.DefaultParameterNameProvider;
import org.hibernate.validator.internal.engine.cascading.ValueExtractors;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests for retrieval of type annotation level constraints.
 *
 * @author Khalid Alqinyah
 */
public class TypeAnnotationMetaDataRetrievalTest {

	private AnnotationMetaDataProvider provider;

	@BeforeClass
	public void setup() {
		provider = new AnnotationMetaDataProvider(
				new ConstraintHelper(),
				new TypeResolutionHelper(),
				new ExecutableParameterNameProvider( new DefaultParameterNameProvider() ),
				new ValueExtractors( Collections.emptyList() ),
				new AnnotationProcessingOptionsImpl()
		);
	}

	@Test
	public void testFieldTypeArgument() throws Exception {
		List<BeanConfiguration<? super A>> beanConfigurations = provider.getBeanConfigurationForHierarchy(
				A.class
		);

		ConstrainedField field = findConstrainedField( beanConfigurations, A.class, "names" );
		assertThat( field.getTypeArgumentConstraints().size() ).isEqualTo( 2 );
		assertThat( getAnnotationsTypes( field.getTypeArgumentConstraints() ) ).contains(
				NotNull.class, NotBlank.class
		);
	}

	@Test
	public void testGetterTypeArgument() throws Exception {
		List<BeanConfiguration<? super B>> beanConfigurations = provider.getBeanConfigurationForHierarchy(
				B.class
		);

		ConstrainedExecutable executable = findConstrainedMethod( beanConfigurations, B.class, "getNames" );
		assertThat( executable.getTypeArgumentConstraints().size() ).isEqualTo( 2 );
		assertThat( getAnnotationsTypes( executable.getTypeArgumentConstraints() ) ).contains(
				NotNull.class, NotBlank.class
		);
	}

	@Test
	public void testReturnValueTypeArgument() throws Exception {
		List<BeanConfiguration<? super C>> beanConfigurations = provider.getBeanConfigurationForHierarchy(
				C.class
		);

		ConstrainedExecutable executable = findConstrainedMethod( beanConfigurations, C.class, "returnNames" );
		assertThat( executable.getTypeArgumentConstraints().size() ).isEqualTo( 2 );
		assertThat( getAnnotationsTypes( executable.getTypeArgumentConstraints() ) ).contains(
				NotNull.class, NotBlank.class
		);
	}

	@Test
	public void testExecutableParameterTypeArgument() throws Exception {
		List<BeanConfiguration<? super D>> beanConfigurations = provider.getBeanConfigurationForHierarchy(
				D.class
		);

		ConstrainedExecutable executable = findConstrainedMethod(
				beanConfigurations,
				D.class,
				"setValues",
				String.class,
				Integer.class,
				List.class
		);
		ConstrainedParameter parameter = executable.getParameterMetaData( 2 );
		assertThat( parameter.getTypeArgumentConstraints().size() ).isEqualTo( 2 );
		assertThat( getAnnotationsTypes( parameter.getTypeArgumentConstraints() ) ).contains(
				NotNull.class, NotBlank.class
		);
	}

	@Test
	public void testConstructorParameterTypeArgument() throws Exception {
		List<BeanConfiguration<? super E>> beanConfigurations = provider.getBeanConfigurationForHierarchy(
				E.class
		);

		ConstrainedExecutable executable = findConstrainedConstructor(
				beanConfigurations,
				E.class,
				String.class,
				Integer.class,
				List.class
		);

		ConstrainedParameter parameter = executable.getParameterMetaData( 2 );
		assertThat( parameter.getTypeArgumentConstraints().size() ).isEqualTo( 2 );
		assertThat( getAnnotationsTypes( parameter.getTypeArgumentConstraints() ) ).contains(
				NotNull.class, NotBlank.class
		);
	}

	private List<Class<? extends Annotation>> getAnnotationsTypes(Collection<MetaConstraint<?>> metaConstraints) {
		return metaConstraints.stream()
			.map( m -> m.getDescriptor().getAnnotationType() )
			.collect( Collectors.toList() );
	}

	private <T> ConstrainedField findConstrainedField(Iterable<BeanConfiguration<? super T>> beanConfigurations,
			Class<? super T> clazz, String fieldName) throws Exception {
		return (ConstrainedField) findConstrainedElement( beanConfigurations, clazz.getDeclaredField( fieldName ) );
	}

	private <T> ConstrainedExecutable findConstrainedMethod(Iterable<BeanConfiguration<? super T>> beanConfigurations,
			Class<? super T> clazz, String methodName, Class<?>... parameterTypes)
			throws Exception {
		return (ConstrainedExecutable) findConstrainedElement(
				beanConfigurations,
				clazz.getMethod( methodName, parameterTypes )
		);
	}

	private <T> ConstrainedExecutable findConstrainedConstructor(
			Iterable<BeanConfiguration<? super T>> beanConfigurations, Class<? super T> clazz,
			Class<?>... parameterTypes) throws Exception {
		return (ConstrainedExecutable) findConstrainedElement(
				beanConfigurations,
				clazz.getConstructor( parameterTypes )
		);
	}

	protected ConstrainedElement findConstrainedElement(Iterable<? extends BeanConfiguration<?>> beanConfigurations, Member member) {
		for ( BeanConfiguration<?> oneConfiguration : beanConfigurations ) {
			for ( ConstrainedElement constrainedElement : oneConfiguration.getConstrainedElements() ) {
				if ( member instanceof Executable && constrainedElement instanceof ConstrainedExecutable ) {
					if ( member.equals( ( (ConstrainedExecutable) constrainedElement ).getExecutable() ) ) {
						return constrainedElement;
					}
				}
				else if ( member instanceof Field && constrainedElement instanceof ConstrainedField ) {
					if ( member.equals( ( (ConstrainedField) constrainedElement ).getField() ) ) {
						return constrainedElement;
					}
				}
			}
		}

		throw new RuntimeException( "Found no constrained element for " + member );
	}

	static class A {
		@Valid
		List<@NotNull @NotBlank String> names;
	}

	static class B {
		@Valid
		public List<@NotNull @NotBlank String> getNames() {
			return Collections.emptyList();
		}
	}

	static class C {
		@Valid
		public List<@NotNull @NotBlank String> returnNames() {
			return Collections.emptyList();
		}
	}

	static class D {
		public void setValues(String s, Integer i, @Valid List<@NotNull @NotBlank String> numbers) {

		}
	}

	static class E {
		public E(String s, Integer i, @Valid List<@NotNull @NotBlank String> numbers) {

		}
	}
}
