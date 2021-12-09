package com.docusign.controller;

import com.docusign.controller.eSignature.examples.EnvelopeHelpers;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Arrays;

@Controller
@RequestMapping("/sign")
public class DocsignerController {

    private static final String DOCUMENT_FILE_NAME = "World_Wide_Corp_lorem.pdf";
    private static final String DOCUMENT_NAME = "Lorem Ipsum";
    private static final int ANCHOR_OFFSET_Y = 20;
    private static final int ANCHOR_OFFSET_X = 10;
    private static final String SIGNER_CLIENT_ID = "1000";

    private static String accountId;

    public Object process(String signerName, String signerEmail, String executionArn) throws IOException {

        // Step 1. Create the envelope definition
        EnvelopeDefinition envelope = makeEnvelope(signerEmail, signerName, executionArn);

//        // Step 2. Call DocuSign to create the envelope
//        ApiClient apiClient = createApiClient(session.getBasePath(), user.getAccessToken());
//        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
//        EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envelope);
//
//        String envelopeId = envelopeSummary.getEnvelopeId();
//        session.setEnvelopeId(envelopeId);
//
//        // Step 3. create the recipient view, the embedded signing
//        RecipientViewRequest viewRequest = makeRecipientViewRequest(signerEmail, signerName);
//        ViewUrl viewUrl = envelopesApi.createRecipientView(accountId, envelopeId, viewRequest);

        // Step 4. Redirect the user to the embedded signing
        // Don't use an iFrame!
        // State can be stored/recovered using the framework's session or a
        // query parameter on the returnUrl (see the makeRecipientViewRequest method)
        //return new RedirectView(viewUrl.getUrl());
        return null;
    }

    private static EnvelopeDefinition makeEnvelope(String signerEmail, String signerName, String executionArn) throws IOException {
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
                .fieldId("executionArn").name("executionArn").value(executionArn));
        envelopeDefinition.setCustomFields(customFields);

        return envelopeDefinition;
    }


}
