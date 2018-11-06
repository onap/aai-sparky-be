package org.onap.aai.sparky.personalization;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.personalization.config.PersonalizationConfig;



public class PersonalizationServiceProviderTest {

  private PersonalizationServiceProvider personalizationServiceProvider;
  private Exchange mockExchange;
  private Message mockRequestMessage;
  private Message mockResponseMessage;
  private PersonalizationConfig mockPersonalizationConfig;


  @Before
  public void init() throws Exception {

    mockExchange = Mockito.mock(Exchange.class);
    mockRequestMessage = Mockito.mock(Message.class);
    mockResponseMessage = Mockito.mock(Message.class);
    mockPersonalizationConfig = Mockito.mock(PersonalizationConfig.class);
    personalizationServiceProvider = new PersonalizationServiceProvider(mockPersonalizationConfig);


    Mockito.when(mockExchange.getIn()).thenReturn(mockRequestMessage);
    Mockito.when(mockExchange.getOut()).thenReturn(mockResponseMessage);

   
  }
  

}
