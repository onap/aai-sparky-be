package org.onap.aai.sparky;


import java.util.List;

import org.junit.Test;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.filters.FilterChain;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class SparkyPojoTest {
//The package to be tested
  private String packageName = "org.onap.aai.sparky";
  private List<PojoClass> pojoClasses;

@Test
  public void validateAnalytics() {
  
  String packageName = "org.onap.aai.sparky.analytics";
  List<PojoClass> analyticsPojoClasses;
  
  FilterChain filterChainByClassNameForAnalytics = new FilterChain(new FilterByContainsClassName("AveragingRingBuffer"),
      new FilterByContainsClassName("AbstractStatistics"), new FilterByContainsClassName("HistogramSampler"),
      new FilterByContainsClassName("Test"));
  analyticsPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForAnalytics);
  validateAll(analyticsPojoClasses);
  
  }

@Test
public void validateAAIConfig() {
  
  String packageName = "org.onap.aai.sparky.dal.aai.config";
  List<PojoClass> aaiConfigPojoClasses;
  // activeinventory config   mught come back
  FilterChain filterChainByClassNameForConfig = new FilterChain(new FilterByContainsClassName("ActiveInventoryConfig"),
      new FilterByContainsClassName("Test"));
  aaiConfigPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForConfig);
  validateAll(aaiConfigPojoClasses);
  
 
  
}


@Test
public void validateElasticSearch(){
  
  String packageName = "org.onap.aai.sparky.dal.elasticsearch";
  List<PojoClass> elasticSearchPojoClasses;
  
  FilterChain filterChainByClassNameForElasticSearch = new FilterChain(new FilterByContainsClassName("ElasticSearchEntityStatistics"),
      new FilterByContainsClassName("Test"));
  elasticSearchPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForElasticSearch);
  validateAll(elasticSearchPojoClasses);
  
 
}


@Test
public void validateElasticSearchEntity(){
  
  String packageName = "org.onap.aai.sparky.dal.elasticsearch.entity";
  List<PojoClass> elasticSearchConfigPojoClasses;
  
  //FilterChain filterChainByClassNameForElasticSearchConfig = new FilterChain(new FilterByContainsClassName("ElasticSearchEntityStatistics"),
    //  new FilterByContainsClassName("Test"));
  elasticSearchConfigPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,null);
  validateAll(elasticSearchConfigPojoClasses);
  
}


@Test
public void validateRest(){
  
  String packageName = "org.onap.aai.sparky.dal.rest";
  List<PojoClass> restPojoClasses;
  
  FilterChain filterChainByClassNameForRest = new FilterChain(new FilterByContainsClassName("RestfulDataAccessor"),
      new FilterByContainsClassName("Test"),new FilterByContainsClassName("RestOperationalStatistics"),
      new FilterByContainsClassName("RestClientBuilder"));
  restPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForRest);
  validateAll(restPojoClasses);
}


@Test
public void validateSASEntity(){

 String packageName = "org.onap.aai.sparky.dal.sas.entity";
  List<PojoClass> sasEntityPojoClasses;
  
  sasEntityPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,null);
  validateAll(sasEntityPojoClasses);
  
}


@Test
public void validateSecurity(){

 String packageName = "org.onap.aai.sparky.security";
  List<PojoClass> securityPojoClasses;
  
  FilterChain filterChainByClassNameForSecurity = new FilterChain(new FilterByContainsClassName("SecurityContextFactoryImpl"),
  new FilterByContainsClassName("Test"), new FilterByContainsClassName("UserManager"),
  new FilterByContainsClassName("RolesConfig"),new FilterByContainsClassName("PortalAuthenticationConfig"));
  securityPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForSecurity);
  validateAll(securityPojoClasses);
}


@Test
public void validateSecurityPortal(){

 String packageName = "org.onap.aai.sparky.security.portal";
  List<PojoClass> securityPortalPojoClasses;
  
  FilterChain filterChainByClassNameForSecurityPortal = new FilterChain(new FilterByContainsClassName("Test"),
   new FilterByContainsClassName("UserManager"),new FilterByContainsClassName("RolesConfig"),
   new FilterByContainsClassName("PortalAuthenticationConfig"));
  securityPortalPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForSecurityPortal);
  validateAll(securityPortalPojoClasses);
}


@Test
public void validateSynchronizer(){

 String packageName = "org.onap.aai.sparky.synchronizer";
  List<PojoClass> synchronizerPojoClasses;
  
  FilterChain filterChainByClassNameForSynchronizer = new FilterChain(new FilterByContainsClassName("Test"),
     new FilterByContainsClassName("AggregationSynchronizer"),new FilterByContainsClassName("SearchableEntitySynchronizer"),
     new FilterByContainsClassName("AutosuggestionSynchronizer"),new FilterByContainsClassName("CrossEntityReferenceSynchronizer"),
     new FilterByContainsClassName("SyncController"),new FilterByContainsClassName("SyncHelper"),
     new FilterByContainsClassName("TransactionRateController"),new FilterByContainsClassName("AggregationSuggestionSynchronizer"),
     new FilterByContainsClassName("AbstractEntitySynchronizer"),new FilterByContainsClassName("SynchronizerConfiguration"));
  synchronizerPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForSynchronizer);
  validateAll(synchronizerPojoClasses);
}

@Test
public void validateUtil(){

 String packageName = "org.onap.aai.sparky.util";
  List<PojoClass> utilPojoClasses;
  
  FilterChain filterChainByClassNameForUtil = new FilterChain(new FilterByContainsClassName("KeystoreBuilder"),
      new FilterByContainsClassName("Test"),new FilterByContainsClassName("HttpServletHelper"),new FilterByContainsClassName("NodeUtils"),
      new FilterByContainsClassName("CaptureLoggerAppender"),new FilterByContainsClassName("ElasticEntitySummarizer"),
      new FilterByContainsClassName("ElasticGarbageInjector"),new FilterByContainsClassName("SuggestionsPermutation"),
      new FilterByContainsClassName("savingTrustManager"));
  utilPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForUtil);
  validateAll(utilPojoClasses);
}

@Test
public void validateViewAndInspect(){

 String packageName = "org.onap.aai.sparky.viewandinspect";
  List<PojoClass> viewAndInspectPojoClasses;
  
  FilterChain filterChainByClassNameForViewAndInspect = new FilterChain(new FilterByContainsClassName("Test"),
      new FilterByContainsClassName("PerformSelfLinkDetermination"),new FilterByContainsClassName("PerformNodeSelfLinkProcessingTask"),
      new FilterByContainsClassName("ActiveInventoryNode"),new FilterByContainsClassName("NodeProcessingTransaction"),
      new FilterByContainsClassName("VisualizationServlet"),new FilterByContainsClassName("VisualizationService"),
      new FilterByContainsClassName("VisualizationContext"));
  viewAndInspectPojoClasses = PojoClassFactory.getPojoClassesRecursively(packageName,filterChainByClassNameForViewAndInspect);
  validateAll(viewAndInspectPojoClasses);
}

public void validateAll(List<PojoClass> pojoClasses){
  
  Validator validator = ValidatorBuilder.create()
      .with(new SetterMustExistRule(),
            new GetterMustExistRule())
      .with(new SetterTester(),
            new GetterTester())
      .build();
validator.validate(pojoClasses);
}

}