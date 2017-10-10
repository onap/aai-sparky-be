package org.onap.aai.sparky.util;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.filters.FilterChain;
import com.openpojo.reflection.filters.FilterClassName;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class UtilPojoTest {
  private String packageName = "org.onap.aai.sparky.util";
  private List<PojoClass> pojoClasses;
  
  @Before
  public void setup() {
    // Get all classes recursively under package
    FilterChain filterChainByClassName = 
        new FilterChain(new FilterClassName("\\w*KeystoreBuilder$"), new FilterClassName("\\w*savingTrustManager$"));
    
    pojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName, filterChainByClassName);
  }

  @Test
    public void validate() {
      Validator validator = ValidatorBuilder.create()
                              .with(new SetterMustExistRule(),
                                    new GetterMustExistRule())
                              .with(new SetterTester(),
                                    new GetterTester())
                              .build();
      validator.validate(pojoClasses);
    }

}
