/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.messageinterpolation;

import java.util.List;
import java.util.Locale.LanguageRange;

import javax.validation.MessageInterpolator;

import org.hibernate.validator.Incubating;

/**
 * Hibernate Validator specific extension of {@link MessageInterpolator}.
 *
 * @author Guillaume Smet
 * @since 6.1.1
 */
@Incubating
public interface HibernateMessageInterpolator extends MessageInterpolator {

	/**
	 * Interpolates the message template based on the constraint validation context.
	 * The {@code Locale} used is provided as a parameter.
	 *
	 * @param messageTemplate the message to interpolate
	 * @param context contextual information related to the interpolation
	 * @param languagePriorities the language preferences targeted for the message
	 *
	 * @return interpolated error message
	 */
	String interpolate(String messageTemplate, Context context,  List<LanguageRange> languagePriorities);
}
