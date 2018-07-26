package com.javawebscrapinghandbook;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/hnitems". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/hnitems
     * 2. curl {your host}/api/hnitems?pageNumber=HTTP%20Query
     */
    @FunctionName("hnitems")
    public HttpResponseMessage<String> hnitems(
            @HttpTrigger(name = "req", methods = {"get"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String pageNumber = request.getQueryParameters().get("pageNumber");
        
        if (pageNumber == null) {
            return request.createResponse(400, "Please pass a pageNumber on the query string");
        }else if(!StringUtils.isNumeric(pageNumber)) {
        	return request.createResponse(400, "Please pass a numeric pageNumber on the query string");
        }else {
        	HNScraper scraper = new HNScraper();
    		String json;
			try {
				json = scraper.scrape(pageNumber);
			} catch (JsonProcessingException e) {
				context.getLogger().info(e.getMessage());
				return request.createResponse(500, "Internal Server Error while processing HN items");
			}
            return request.createResponse(200, json);
        }
    }
}
