package org.onap.aai.sparky.synchronizer.entity;

import org.junit.Test;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.PojoValidator;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class IndexableEntityTest {
// The package to be tested
	  private String packageName = "org.onap.aai.sparky.synchronizer.entity";

	  /**
     * @return the packageName
     */
    public String getPackageName() {
      return packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    @Test
	  public void validate() {
	    Validator validator = ValidatorBuilder.create()
	                            .with(new SetterMustExistRule(),
	                                  new GetterMustExistRule())
	                            .with(new SetterTester(),
	                                  new GetterTester())
	                            .build();
	    validator.validate(packageName);
	  }
}
