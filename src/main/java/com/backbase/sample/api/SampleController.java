package com.backbase.sample.api;

import com.backbase.buildingblocks.backend.security.accesscontrol.accesscontrol.AccessControlValidator;
import com.backbase.buildingblocks.backend.security.accesscontrol.accesscontrol.AccessResourceType;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.buildingblocks.presentation.errors.ForbiddenException;
import java.util.Collections;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"/client-api/v1/sample", "/v1/sample"})
@RestController
@AllArgsConstructor
public class SampleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleController.class);
    private AccessControlValidator accessControlValidator;

    /**
     * Permission check with preauthorize annotation example
     *
     * resource: Legal Entity, business function: Manage Legal Entities, privileges: view,create
     */
    @PreAuthorize("checkPermission('Legal Entity', 'Manage Legal Entities', {'view','create'})")
    @GetMapping(
        produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public void sampleEndpointThatRequiresUserToHavePermissionsToViewCreateLegalEnitites() {
        LOGGER.info("Preauthorize annotation have checked that user has permissions to view/create legal entities");
        // continue custom implementation ...
    }

    /**
     * Checks if current user has no access to service agreement.
     *
     * @param serviceAgreementId service agreement internal id
     * @param accessResourceType one of the following values:NONE, USER, ACCOUNT, USER_OR_ACCOUNT, USER_AND_ACCOUNT
     */
    @GetMapping(
        value = {"/example"},
        produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public void sampleEndpointThatRequiresUserToHaveAccessToServiceAgreement(
        @RequestParam(value = "serviceAgreementId", required = true) String serviceAgreementId,
        @RequestParam(value = "accessResourceType", required = true) String accessResourceType) {

        LOGGER.info("Check access to resources");
        if (accessControlValidator.userHasNoAccessToServiceAgreement(
            serviceAgreementId,
            AccessResourceType.valueOf(accessResourceType))) {

            throw new ForbiddenException()
                .withMessage("Forbidden")
                .withErrors(Collections.singletonList(new Error()
                    .withMessage("User has no access to service agreement")));
        }
        // continue custom implementation ...
    }

}
