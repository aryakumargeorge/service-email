package com.manywho.services.email.controllers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.email.actions.SendEmail;
import com.manywho.services.email.actions.SendEmailSimple;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.managers.EmailManager;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

@Path("/actions")
@Consumes("application/json")
@Produces("application/json")
public class SendEmailController extends AbstractController {

    private EmailManager emailManager;

    @Inject
    public SendEmailController(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    @Path("/email")
    @POST
    public ServiceResponse sendEmail(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        SendEmail mailParameters = this.parseInputs(serviceRequest,  SendEmail.class);

        emailManager.sendEmail(configuration, mailParameters, isDebugActive(serviceRequest.getPlayerUri()));
        return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
    }

    @Path("/email-simple")
    @POST
    public ServiceResponse sendEmailSimple(ServiceRequest serviceRequest) throws Exception {
        Configuration configuration = this.parseConfigurationValues(serviceRequest, Configuration.class);
        SendEmailSimple mailParameters = this.parseInputs(serviceRequest,  SendEmailSimple.class);

        emailManager.sendEmailSimple(configuration, mailParameters, isDebugActive(serviceRequest.getPlayerUri()));

        return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
    }

    private boolean isDebugActive(String uri) throws URISyntaxException {
        List<NameValuePair> parameters  = URLEncodedUtils.parse(new URI(uri), "UTF-8");
        for (NameValuePair p:parameters) {
            if(Objects.equals(p.getName(), "mode") && (Objects.equals(p.getValue(), "DEBUG") || Objects.equals(p.getValue(), "DEBUG_STEPTHROUGH"))) {
                return true;
            }
        }

        return false;
    }
}
