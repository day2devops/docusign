package com.docusign;

import com.docusign.common.WorkArguments;
import com.docusign.controller.eSignature.examples.AbstractEsignatureController;
import com.docusign.controller.eSignature.examples.EnvelopeHelpers;
import com.docusign.core.model.Session;
import com.docusign.core.model.User;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Use embedded signing.<br/>
 * This example sends an envelope, and then uses embedded signing
 * for the first signer. Embedded signing provides a smoother user experience
 * for the signer: the DocuSign signing is initiated from your site.
 */
@Controller
@RequestMapping("/eg001")
public class EG001ControllerEmbeddedSigning extends AbstractEsignatureController {

    private static final String DOCUMENT_FILE_NAME = "World_Wide_Corp_lorem.pdf";
    private static final String DOCUMENT_NAME = "Lorem Ipsum";
    private static final int ANCHOR_OFFSET_Y = 20;
    private static final int ANCHOR_OFFSET_X = 10;
    private static final String SIGNER_CLIENT_ID = "1000";

    private final Session session;
    private final User user;


    @Autowired
    public EG001ControllerEmbeddedSigning(DSConfiguration config, Session session, User user) {
        super(config, "eg001", "Use embedded signing");
        this.session = session;
        this.user = user;
    }

    @Override
    protected Object doWork(WorkArguments args, ModelMap model,
                            HttpServletRequest request, HttpServletResponse response) throws ApiException, IOException {
        String signerName = args.getSignerName();
        String signerEmail = args.getSignerEmail();
        String accountId = session.getAccountId();
        accountId = "5fe81558-e430-4398-af5e-fab120b6f4b8";
        String basePath = "https://demo.docusign.net/restapi";

        String uuid = request.getParameter("uuid");
        System.out.println("****************************");
        System.out.println("From request: " + request.getParameter("token"));
        System.out.println("Account ID: " + accountId);
        System.out.println("Base Path: " + session.getBasePath());
        System.out.println("****************************");


        // Step 1. Create the envelope definition
        EnvelopeDefinition envelope = makeEnvelope(signerEmail, signerName, uuid);

        System.out.println("#################");
        System.out.println(user.getAccessToken());
        System.out.println("#################");

        String staticToken = "eyJ0eXAiOiJNVCIsImFsZyI6IlJTMjU2Iiwia2lkIjoiNjgxODVmZjEtNGU1MS00Y2U5LWFmMWMtNjg5ODEyMjAzMzE3In0.AQsAAAABAAUABwCAdzXQ3LvZSAgAgLdY3h-82UgCAPmJjal9vbVCkuOqmJn_eYMVAAEAAAAYAAEAAAAFAAAADQAkAAAAYTIyNzhkMTktOTQ2NS00OTY0LWEwMjMtYzQ5NjVlODc2OTVlIgAkAAAAYTIyNzhkMTktOTQ2NS00OTY0LWEwMjMtYzQ5NjVlODc2OTVlEgABAAAACwAAAGludGVyYWN0aXZlMACASgTP3LvZSDcAwA5jptAqS0Wl6WiqqiI3tA.B1zon-ci5Z6ZsqNneecR3jahJuFnL90OuR3HJKQljI0cffJ5p7m4eglYmGpXDYZXtWihHzFr9F4ZPOXm0d2zD0NgjcTlCRCOXWPARzZunOkWy0VpwvUhXBNc7GGaZNDFelBnffB5uA0NenVvBbzaKyCqnbOMsMtwAXzZWcpHSwIfI8aRycJaN90zvFhIUpBVKSXKMgvXZjVSTxWhayiR4T9xZd2FMb5SjzIGb0h1BQf66WrQTuE21k9wOlI51ybgbGrgHBx_BQhdyAaLdEaW1O5TEnk8p3lTN1gt2eXQXqL9RS_nqW7X77uHnVYMAIEnseEZQuJAYENeZKDlRRmFQw";
        //staticToken = System.getenv("access_token");

        // Step 2. Call DocuSign to create the envelope
        ApiClient apiClient = createApiClient(basePath, staticToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envelope);

        String envelopeId = envelopeSummary.getEnvelopeId();
        session.setEnvelopeId(envelopeId);

        // Step 3. create the recipient view, the embedded signing
        RecipientViewRequest viewRequest = makeRecipientViewRequest(signerEmail, signerName);
        ViewUrl viewUrl = envelopesApi.createRecipientView(accountId, envelopeId, viewRequest);

        // Step 4. Redirect the user to the embedded signing
        // Don't use an iFrame!
        // State can be stored/recovered using the framework's session or a
        // query parameter on the returnUrl (see the makeRecipientViewRequest method)
        return new RedirectView(viewUrl.getUrl());
    }

    private RecipientViewRequest makeRecipientViewRequest(String signerEmail, String signerName) {
        RecipientViewRequest viewRequest = new RecipientViewRequest();
        // Set the url where you want the recipient to go once they are done signing
        // should typically be a callback route somewhere in your app.
        // The query parameter is included as an example of how
        // to save/recover state information during the redirect to
        // the DocuSign signing. It's usually better to use
        // the session mechanism of your web framework. Query parameters
        // can be changed/spoofed very easily.
        viewRequest.setReturnUrl("http://localhost:8282/tasks");

        // How has your app authenticated the user? In addition to your app's
        // authentication, you can include authenticate steps from DocuSign.
        // Eg, SMS authentication
        viewRequest.setAuthenticationMethod("none");

        // Recipient information must match embedded recipient info
        // we used to create the envelope.
        viewRequest.setEmail(signerEmail);
        viewRequest.setUserName(signerName);
        viewRequest.setClientUserId(SIGNER_CLIENT_ID);

        // DocuSign recommends that you redirect to DocuSign for the
        // embedded signing. There are multiple ways to save state.
        // To maintain your application's session, use the pingUrl
        // parameter. It causes the DocuSign signing web page
        // (not the DocuSign server) to send pings via AJAX to your app.
        // NOTE: The pings will only be sent if the pingUrl is an https address
        viewRequest.setPingFrequency("600"); // seconds
        viewRequest.setPingUrl(config.getDsPingUrl());

        return viewRequest;
    }

    private static EnvelopeDefinition makeEnvelope(String signerEmail, String signerName, String uuid) throws IOException {
        // Create a signer recipient to sign the document, identified by name and email
        // We set the clientUserId to enable embedded signing for the recipient
        Signer signer = new Signer();
        signer.setEmail(signerEmail);
        signer.setName(signerName);
        signer.clientUserId(SIGNER_CLIENT_ID);
        signer.recipientId("1");
        signer.setTabs(EnvelopeHelpers.createSingleSignerTab("/sn1/", ANCHOR_OFFSET_Y, ANCHOR_OFFSET_X));

        // Add the recipient to the envelope object
        Recipients recipients = new Recipients();
        recipients.setSigners(Arrays.asList(signer));

        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        envelopeDefinition.setRecipients(recipients);
        Document doc = EnvelopeHelpers.createDocumentFromFile(DOCUMENT_FILE_NAME, DOCUMENT_NAME, "3");
        envelopeDefinition.setDocuments(Arrays.asList(doc));
        // Request that the envelope be sent by setting |status| to "sent".
        // To request that the envelope be created as a draft, set to "created"
        envelopeDefinition.setStatus(EnvelopeHelpers.ENVELOPE_STATUS_SENT);
        envelopeDefinition.setNotificationUri("https://3nwmi3ilvg.execute-api.us-east-2.amazonaws.com/dev/callback");

        CustomFields customFields = new CustomFields();

        customFields.addTextCustomFieldsItem(new TextCustomField()
                .fieldId("uuid").name("uuid").value(uuid));
        envelopeDefinition.setCustomFields(customFields);

        return envelopeDefinition;
    }
}
