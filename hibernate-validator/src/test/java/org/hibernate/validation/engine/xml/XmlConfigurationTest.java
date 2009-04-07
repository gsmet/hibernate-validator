// $Id:$
/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.validation.engine.xml;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.testng.annotations.Test;

import static org.hibernate.validation.util.TestUtil.assertNumberOfViolations;
import static org.hibernate.validation.util.TestUtil.getValidator;
import org.hibernate.validation.util.TestUtil;

/**
 * @author Hardy Ferentschik
 */
public class XmlConfigurationTest {

	@Test
	public void testValidatorResolutionForMinMax() {
		Validator validator = getValidator();

		User user = new User();
		Set<ConstraintViolation<User>> constraintViolations = validator.validate( user );
		assertNumberOfViolations( constraintViolations, 1 );
		TestUtil.assertConstraintViolation( constraintViolations.iterator().next(), "Message from xml" );

		user.setConsistent( true );
		constraintViolations = validator.validate( user );
		assertNumberOfViolations( constraintViolations, 0 );
	}
}
