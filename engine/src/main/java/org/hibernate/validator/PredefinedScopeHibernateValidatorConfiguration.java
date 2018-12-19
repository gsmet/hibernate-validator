/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator;

import java.util.Locale;
import java.util.Set;

import org.hibernate.validator.metadata.BeanMetaDataClassNormalizer;

/**
 * Extension of {@link HibernateValidatorConfiguration} with additional methods dedicated to defining the predefined
 * scope of bean validation e.g. validated classes, constraint validators...
 *
 * @author Guillaume Smet
 *
 * @since 6.1
 */
@Incubating
public interface PredefinedScopeHibernateValidatorConfiguration extends BaseHibernateValidatorConfiguration<PredefinedScopeHibernateValidatorConfiguration> {

	@Incubating
	PredefinedScopeHibernateValidatorConfiguration initializeBeanMetaData(Set<Class<?>> beanClassesToInitialize);

	@Incubating
	PredefinedScopeHibernateValidatorConfiguration initializeLocales(Set<Locale> locales);

	@Incubating
	PredefinedScopeHibernateValidatorConfiguration beanMetaDataClassNormalizer(BeanMetaDataClassNormalizer beanMetaDataClassNormalizer);
}
