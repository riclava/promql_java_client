package com.bdwise.prometheus.client;

import java.net.MalformedURLException;
import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.bdwise.prometheus.client.builder.InstantQueryBuilder;
import com.bdwise.prometheus.client.builder.QueryBuilderType;
import com.bdwise.prometheus.client.builder.RangeQueryBuilder;
import com.bdwise.prometheus.client.converter.ConvertUtil;
import com.bdwise.prometheus.client.converter.query.DefaultQueryResult;
import com.bdwise.prometheus.client.converter.query.MatrixData;
import com.bdwise.prometheus.client.converter.query.VectorData;

import junit.framework.TestCase;

public class PromqlTest extends TestCase {
	private final static String TARGET_SERVER = "http://52.192.4.59:30900";
	
	private RestTemplate template = null;
	
	@Override
	protected void setUp() throws Exception {
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectTimeout(1000);
		httpRequestFactory.setReadTimeout(2000);
		HttpClient httpClient = HttpClientBuilder.create()
		 .setMaxConnTotal(100)
		 .setMaxConnPerRoute(10)
		 .build();
		httpRequestFactory.setHttpClient(httpClient);
		
		template = new RestTemplate(httpRequestFactory);
	}
	
	public void testSimpleRangeQuery() throws MalformedURLException {
		RangeQueryBuilder rangeQueryBuilder =  QueryBuilderType.RangeQuery.newInstance(TARGET_SERVER);
		URI targetUri = rangeQueryBuilder.withQuery("irate(received_api_call_total[60s])")
		                 .withStartEpochTime(System.currentTimeMillis() / 1000 - 60*10)
		                 .withEndEpochTime(System.currentTimeMillis() / 1000)
		                 .withStepTime("60s")
		                 .build();
		
		System.out.println(targetUri.toURL().toString());
		
		String rtVal = template.getForObject(targetUri, String.class);

		
		
		
		DefaultQueryResult<MatrixData> result = ConvertUtil.convertQueryResultString(rtVal);

		
		System.out.println(result);	
	}
	
	public void testSimpleInstantQuery() throws MalformedURLException {
		InstantQueryBuilder iqb = QueryBuilderType.InstantQuery.newInstance(TARGET_SERVER);
		URI targetUri = iqb.withQuery("irate(received_api_call_total[60s])").build();
		System.out.println(targetUri.toURL().toString());
		
		
		String rtVal = template.getForObject(targetUri, String.class);


		DefaultQueryResult<VectorData> result = ConvertUtil.convertQueryResultString(rtVal);

		
		System.out.println(result);		
	}	
}
