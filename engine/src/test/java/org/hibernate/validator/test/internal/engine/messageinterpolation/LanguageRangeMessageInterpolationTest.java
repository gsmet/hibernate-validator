/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.test.internal.engine.messageinterpolation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolator;
import org.hibernate.validator.testutil.TestForIssue;
import org.testng.annotations.Test;

@TestForIssue(jiraKey = "HV-1748")
public class LanguageRangeMessageInterpolationTest {

	@Test
	public void testLanguageRangeSupport() throws NoSuchMethodException, SecurityException {
		ValidatorFactory validatorFactory = getValidatorFactoryWithInitializedLocales( Locale.FRANCE, new Locale( "es", "ES" ) );
		HibernateMessageInterpolator messageInterpolator = (HibernateMessageInterpolator) validatorFactory.getMessageInterpolator();

		assertThat( messageInterpolator.interpolate( "{javax.validation.constraints.AssertFalse.message}", new TestContext(),
				LanguageRange.parse( "fr-FR,fr;q=0.9" ) ) )
						.isEqualTo( "doit avoir la valeur faux" );
	}

	@Test
	public void testCascadePriorities() {
		ValidatorFactory validatorFactory = getValidatorFactoryWithInitializedLocales( Locale.FRANCE, Locale.forLanguageTag( "es" ) );
		HibernateMessageInterpolator messageInterpolator = (HibernateMessageInterpolator) validatorFactory.getMessageInterpolator();

		assertThat( messageInterpolator.interpolate( "{javax.validation.constraints.AssertFalse.message}", new TestContext(),
				LanguageRange.parse( "hr-HR,hr;q=0.9,es;q=0.7" ) ) )
						.isEqualTo( "debe ser falso" );
	}

	@Test
	public void testFallbackToDefault() throws NoSuchMethodException, SecurityException {
		// Defaults to en when we don't define a default as we launch Surefire with the en locale
		ValidatorFactory validatorFactory = getValidatorFactoryWithInitializedLocales( new Locale( "es", "ES" ) );
		HibernateMessageInterpolator messageInterpolator = (HibernateMessageInterpolator) validatorFactory.getMessageInterpolator();

		assertThat( messageInterpolator.interpolate( "{javax.validation.constraints.AssertFalse.message}", new TestContext(),
				LanguageRange.parse( "hr-HR,hr;q=0.9" ) ) )
						.isEqualTo( "must be false" );

		// Defaults to fr_FR if we define it as the default locale
		validatorFactory = getValidatorFactoryWithDefaultLocaleAndInitializedLocales( Locale.FRANCE, Locale.forLanguageTag( "es_ES" ) );
		messageInterpolator = (HibernateMessageInterpolator) validatorFactory.getMessageInterpolator();

		assertThat( messageInterpolator.interpolate( "{javax.validation.constraints.AssertFalse.message}", new TestContext(),
				LanguageRange.parse( "hr-HR,hr;q=0.9" ) ) )
						.isEqualTo( "doit avoir la valeur faux" );
	}

	private static ValidatorFactory getValidatorFactoryWithInitializedLocales(Locale... locales) {
		ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
				.configure()
				.locales( new HashSet<>( Arrays.asList( locales ) ) )
				.buildValidatorFactory();

		return validatorFactory;
	}

	private static ValidatorFactory getValidatorFactoryWithDefaultLocaleAndInitializedLocales(Locale defaultLocale, Locale... locales) {
		ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
				.configure()
				.locales( new HashSet<>( Arrays.asList( locales ) ) )
				.defaultLocale( defaultLocale )
				.buildValidatorFactory();

		return validatorFactory;
	}

	private static class TestContext implements MessageInterpolator.Context {

		@Override
		public ConstraintDescriptor<?> getConstraintDescriptor() {
			return null;
		}

		@Override
		public Object getValidatedValue() {
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T unwrap(Class<T> type) {
			return (T) this;
		}
	}
}
