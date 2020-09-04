# Implement Access control in a custom capability
## Implement Access Control.

[Example: Implement Access control in a custom capability from Community](https://community.backbase.com/documentation/DBS/latest/entitlements_integrate_into_a_capability)

## Description
This example shows you how to integrate Access Control into another service.
You use Access Control to easily allow specific users to perform clearly defined tasks on a limited group of arrangements.
Permissions are evaluated with an OR operator; the user must be assigned at least one permission to complete the function
 they require. When the user has permissions to perform the specified function, the request proceeds to the requested 
 handler method. Otherwise, HTTP 403 (Forbidden) is returned.
 
## How to use
### 1. Create a new project

Create a new project based on service-sdk-starter-core:

      <parent>
        <artifactId>service-sdk-starter-core</artifactId>
        <groupId>com.backbase.buildingblocks</groupId>
        <version>LATEST</version>
        <relativePath/>
      </parent>

To use access control in your custom service add the following dependency inside your pom.xml:

    <dependency>
        <groupId>com.backbase.dbs.accesscontrol</groupId>
        <artifactId>auth-security-dbs-accesscontrol</artifactId>
        <version>[specify version]</version>
    </dependency>
    
### 2. Integrate Access Control into your code

#### Use the library:

To provide a convenient method for access control using the Entitlements engine, auth-security-dbs-accesscontrol uses the Spring Security `@PreAuthorize` annotation in combination with a custom security expression.

To use the annotation to check permissions, supply a function name, resource name and a privilege:
 
    @PreAuthorize("checkPermission('[your_resource_name]', '[your_function_name]', {'[privilege]'})")

Add the @PreAuthorize annotation on your REST controller handler methods. For example:


    @PreAuthorize("checkPermission('Payments', 'Domestic Payments', {'create'})")
    @RequestMapping(method = {RequestMethod.POST}, value = {""})
    @ResponseStatus(HttpStatus.CREATED)
    public void createPayment(@RequestBody @Valid CreatePaymentPostRequestBody payment){
    //TODO Implement method
    }
    
In this example, a check is made that the user making the request (as identified by the JWT token) has been assigned a function group with a 'create' privilege for a 'Payments' resource and 'Domestic Payments' function.

If the user is privileged to perform the specified function on a resource, the request will proceed with the request handler method. Otherwise, an HTTP 403 Forbidden status code is returned.

### 3. Manually integrate Access Control into your code
To manually verify the configuration, use the service GET methods of the following endpoints:

#### a. Configure DBS auth-security access control:
Make a GET request to: /access-control/service-api/v2/accessgroups/users/permissions?userId=<Internal User ID>&serviceAgreementId=<Service agreement ID>&functionName=<Function Name>&resourceName=<Resource Name>&privileges=<Privilege>

    presentationAccessgroupUsersClient
    .getUserPermissionCheck(permissionsQueryParameters);
    
#### b.Check the list of all privileges:
Retrieve all privileges for a user under single service agreement for legal entity Backbase. Make a GET request to: /access-control/service-api/v2/accessgroups/users/privileges?userId=&<Internal User ID>&serviceAgreementId=<Service agreement ID>&functionName=<Function Name>&resourceName=<Resource>

    ResponseEntity<? extends List<com.backbase.presentation.accessgroup.rest.spec.v2.accessgroups.users.PrivilegesGetResponseBody>> privileges = presentationAccessgroupUsersClient.getPrivileges(permissionsQueryParameters);


#### c.Get the list of arrangements with privileges:
Retrieve a list of arrangements for a user called “Jonathan”. Make a GET request to: /access-control/service-api/v2/accessgroups/users/privileges/arrangements?userId=<Internal User ID>&serviceAgreementId=<Service agreement ID>&functionName=<Function Name>&resourceName=<Resource Name>&privilegeName=<Privilege>

    public ResponseEntity<? extends List<com.backbase.presentation.accessgroup.rest.spec.v2.accessgroups.users.ArrangementPrivilegesGetResponseBody>> arrangementPrivileges = presentationAccessgroupUsersClient.getArrangementPrivileges(permissionsQueryParameters);

#### d.Check the permission for an arrangement:
Check if a user in the service agreement has some permission over a specific arrangement. Make a GET request to: /access-control/service-api/v2/accessgroups/users/user-privileges/arrangements/<arrangement-internal_id>?function=<Function Name>&resource=<Resource Name>&privilege=<Privilege>

    presentationAccessgroupUsersClient.getArrangementPermissionCheck(arrangementId, permissionsQueryParameters);
    
#### e.Check if the user has permissions to access users or arrangements that belong to the list of legal entities:
You should use the method `userHasNoAccessToEntitlementsResource` in the component `AccessControlValidator`. For example:
    
    Boolean hasNoAccess = accessControlValidator.userHasNoAccessToEntitlementResource
    (legalEntities, AccessResourceType.ACCOUNT);

    Boolean hasNoAccess = accessControlValidator.userHasNoAccessToEntitlementResource
    (legalEntities, AccessResourceType.USER);
    
    Boolean hasNoAccess = accessControlValidator.userHasNoAccessToEntitlementResource
    (legalEntities, AccessResourceType.USER_OR_ACCOUNT);
    
#### f.Check if the user has permissions to access users or arrangements that belong to service agreement:
You should use the method `userHasNoAccessToServiceAgreement` in the component `AccessControlValidator`. For example:

    Boolean hasNoAccess = accessControlValidator.userHasNoAccessToServiceAgreement
    (serviceAgreementId, AccessResourceType.ACCOUNT);

    Boolean hasNoAccess = accessControlValidator.userHasNoAccessToServiceAgreement
    (serviceAgreementId, AccessResourceType.USER);

    Boolean hasNoAccess = accessControlValidator.userHasNoAccessToServiceAgreement
    (serviceAgreementId, AccessResourceType.USER_OR_ACCOUNT);


### 4. Create a business function
Depending on your database type, to add a new business function for your service in the Access Control database, update and run one of the following example scripts for your database:

MySQL:

    INSERT INTO `business_function`
    (
      `id`,
      `function_code`,
      `function_name`,
      `resource_code`,
      `resource_name`
    )
    VALUES
      ('1014', 'manage.shadow.limits', 'Manage Shadow Limits', 'limits', 'Limits');
     
    INSERT INTO `applicable_function_privilege`
    (
      `id`,
      `business_function_name`,
      `function_resource_name`,
      `privilege_name`,
      `supports_limit`,
      `business_function_id`,
      `privilege_id`
    )
    VALUES
      ('33', 'Manage Shadow Limits', 'Limits', 'view', '0', '1014', '2'),
      ('34', 'Manage Shadow Limits', 'Limits', 'create', '0', '1014', '3'),
      ('35', 'Manage Shadow Limits', 'Limits', 'edit', '0', '1014', '4'),
      ('36', 'Manage Shadow Limits', 'Limits', 'delete', '0', '1014', '5');
     
    INSERT INTO `assignable_permission_set_item`(
        assignable_permission_set_id,
        function_privilege_id
    )
    VALUES
        (1, '33'),
        (1, '34'),
        (1, '35'),
        (1, '36');


Oracle:

    INSERT INTO BUSINESS_FUNCTION
    (ID, FUNCTION_CODE, FUNCTION_NAME, RESOURCE_CODE, RESOURCE_NAME)
    VALUES
      ('1014', 'manage.shadow.limits', 'Manage Shadow Limits', 'limits', 'Limits');
     
    INSERT INTO APPLICABLE_FUNCTION_PRIVILEGE
    (ID, BUSINESS_FUNCTION_NAME, FUNCTION_RESOURCE_NAME, PRIVILEGE_NAME, SUPPORTS_LIMIT, BUSINESS_FUNCTION_ID, PRIVILEGE_ID)
    VALUES
      ('33', 'Manage Shadow Limits', 'Limits', 'view', '0', '1014', '2');
     
    INSERT INTO APPLICABLE_FUNCTION_PRIVILEGE
    (ID, BUSINESS_FUNCTION_NAME, FUNCTION_RESOURCE_NAME, PRIVILEGE_NAME, SUPPORTS_LIMIT, BUSINESS_FUNCTION_ID, PRIVILEGE_ID)
    VALUES
      ('34', 'Manage Shadow Limits', 'Limits', 'create', '0', '1014', '3');
     
    INSERT INTO APPLICABLE_FUNCTION_PRIVILEGE
    (ID, BUSINESS_FUNCTION_NAME, FUNCTION_RESOURCE_NAME, PRIVILEGE_NAME, SUPPORTS_LIMIT, BUSINESS_FUNCTION_ID, PRIVILEGE_ID)
    VALUES
      ('35', 'Manage Shadow Limits', 'Limits', 'edit', '0', '1014', '4');
     
    INSERT INTO APPLICABLE_FUNCTION_PRIVILEGE
    (ID, BUSINESS_FUNCTION_NAME, FUNCTION_RESOURCE_NAME, PRIVILEGE_NAME, SUPPORTS_LIMIT, BUSINESS_FUNCTION_ID, PRIVILEGE_ID)
    VALUES
      ('36', 'Manage Shadow Limits', 'Limits', 'delete', '0', '1014', '5');
     
    INSERT INTO ASSIGNABLE_PERMISSION_SET_ITEM
    (ASSIGNABLE_PERMISSION_SET_ID, FUNCTION_PRIVILEGE_ID)
    VALUES
        (1, '33');
     
    INSERT INTO ASSIGNABLE_PERMISSION_SET_ITEM
    (ASSIGNABLE_PERMISSION_SET_ID, FUNCTION_PRIVILEGE_ID)
    VALUES
        (1, '34');
     
    INSERT INTO ASSIGNABLE_PERMISSION_SET_ITEM
    (ASSIGNABLE_PERMISSION_SET_ID, FUNCTION_PRIVILEGE_ID)
    VALUES
        (1, '35');
     
    INSERT INTO ASSIGNABLE_PERMISSION_SET_ITEM
    (ASSIGNABLE_PERMISSION_SET_ID, FUNCTION_PRIVILEGE_ID)
    VALUES
        (1, '36');
     
    commit;

Microsoft SqlServer:

    insert into [business_function]
    ( [id], [function_code], [function_name], [resource_code], [resource_name] )
    values
      ('1014', 'manage.shadow.limits', 'Manage Shadow Limits', 'limits', 'Limits');
    go
     
    insert into [applicable_function_privilege]
    (
      [id],
      [business_function_name],
      [function_resource_name],
      [privilege_name],
      [supports_limit],
      [business_function_id],
      [privilege_id]
    )
    values
      ('33', 'Manage Shadow Limits', 'Limits', 'view', '0', '1014', '2'),
      ('34', 'Manage Shadow Limits', 'Limits', 'create', '0', '1014', '3'),
      ('35', 'Manage Shadow Limits', 'Limits', 'edit', '0', '1014', '4'),
      ('36', 'Manage Shadow Limits', 'Limits', 'delete', '0', '1014', '5');
    go
     
    insert into [assignable_permission_set_item]
    (
        [assignable_permission_set_id],
        [function_privilege_id]
    )
    values
        (1, '33'),
        (1, '34'),
        (1, '35'),
        (1, '36');
    go
    
When you insert the applicable_function_privilege, the value of privilege_id should be id from the privilege table. The values are: 2- view, 3 - create, 4 - edit, 5- delete, 6- approve.

After updating the database, restart access-control.

The values in these code examples are sample values only.

#### 1. Follow the business function guidelines
When adding a new Business Function it is crucial to follow the guidelines and naming conventions for adding a custom business function (on a Project). By doing so we can prevent disorganization, clashing with bussines function names used in the product and the breaking of current flows:

| Parameter | Type | Length | Description
|--- | --- |---| ---|
| ID | String | 36 | Description: unique ID in the system <br>  Guidelines: <project_prefix>.<1xxx> <br> Example: bankA.1000 |
| Function_name | String | 32 | Description: name of the business function and unique in the system<br> Guidelines: <project_prefix> <descriptive name><br>Example: bankA PISP|
| Function_code | String | 32 | Description: Unique code used for internationalization on front-end (widget) <br> Guidelines: same as function name in lower case, use dots instead of spaces <br> Example: bankA.pisp <br> On the front-end, you map this code with the descriptive name of the business function. For example, make payments from the business’s bank accounts using third-party websites or software. |
| Resource_name | String | 32 | Description: Logical grouping of business functions (no real usage at the moment) <br> Guidelines: <project_prefix> <group_or_business_function>, if the business function is part of some logical grouping or use the same name as the business function. <br> Example: bankA psd2, bank openbanking, bankA payments, etc. |
| Resource_code | String | 32 | Description: Unique code used for internationalization on front-end (Widget) if required (for now we are not using this at all) <br> Guidelines: same as resource_name in lower case, use dots instead of spaces <br> Example: bankA.psd2, bankA.openbanking, bankA.payments |

#### 1. Display custom business function name on front-end
If you skip this step in the procedure, there is a fallback mechanism in place, which will trigger displaying the business function name entered in the database on front-end.

##### a. Modifications on the following extensions that display Business Function list is required:
`ext-bb-sa-user-privileges-ng, ext-bb-function-access-groups-ng.` Add the following (example using the Manage Shadow Limits busines function) to the messages.json inside the assets folder:

    manage.shadow.limits (function_code): "Configure Shadow Limits"
    
##### b. For the ext-sa-user-privileges-ng extension, additional step is required:

In templates/template.ng.html, find the ui-bb-permissions-modal-ng component and update the businessFunction object inside of data-labels directive with the newly added business function:

    'manage.shadow.limits': ('business.function.manage.shadow.limits' | i18n)

##### c. Business functions on the front-end are grouped based on the resource name. When a business function with a new resource name is added, by default it will be displayed in the group Others. You may configure the newly added business function to be displayed in one of the available groups, by referencing the resource name to the selected group by either of the following methods:

-Update the constants.js file to include the new resource in a group (or creating a new group)

-If you created a new group, update the messages.json file to add internationalization for the new group

