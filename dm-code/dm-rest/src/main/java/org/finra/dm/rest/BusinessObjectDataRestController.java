/*
* Copyright 2015 herd contributors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.finra.dm.rest;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.finra.dm.model.dto.SecurityFunctions;
import org.finra.dm.model.jpa.NotificationEventTypeEntity;
import org.finra.dm.model.api.xml.BusinessObjectData;
import org.finra.dm.model.api.xml.BusinessObjectDataAvailability;
import org.finra.dm.model.api.xml.BusinessObjectDataAvailabilityCollectionRequest;
import org.finra.dm.model.api.xml.BusinessObjectDataAvailabilityCollectionResponse;
import org.finra.dm.model.api.xml.BusinessObjectDataAvailabilityRequest;
import org.finra.dm.model.api.xml.BusinessObjectDataCreateRequest;
import org.finra.dm.model.api.xml.BusinessObjectDataDdl;
import org.finra.dm.model.api.xml.BusinessObjectDataDdlCollectionRequest;
import org.finra.dm.model.api.xml.BusinessObjectDataDdlCollectionResponse;
import org.finra.dm.model.api.xml.BusinessObjectDataDdlRequest;
import org.finra.dm.model.api.xml.BusinessObjectDataInvalidateUnregisteredRequest;
import org.finra.dm.model.api.xml.BusinessObjectDataInvalidateUnregisteredResponse;
import org.finra.dm.model.api.xml.BusinessObjectDataKey;
import org.finra.dm.model.api.xml.BusinessObjectDataVersions;
import org.finra.dm.model.api.xml.S3KeyPrefixInformation;
import org.finra.dm.service.BusinessObjectDataService;
import org.finra.dm.service.NotificationEventService;
import org.finra.dm.service.helper.DmHelper;
import org.finra.dm.ui.constants.UiConstants;

/**
 * The REST controller that handles business object data REST requests.
 */
@RestController
@RequestMapping(value = UiConstants.REST_URL_BASE, produces = {"application/xml", "application/json"})
public class BusinessObjectDataRestController extends DmBaseController
{
    @Autowired
    private BusinessObjectDataService businessObjectDataService;

    @Autowired
    private DmHelper dmHelper;

    @Autowired
    private NotificationEventService notificationEventService;

    /**
     * <p> Gets the S3 key prefix for writing or accessing business object data. </p> <p> This endpoint does not require a namespace. </p>
     *
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionKey the partition key.
     * @param partitionValue the partition value.
     * @param subPartitionValues the sub-partition values.
     * @param businessObjectDataVersion the business object data version.
     * @param createNewVersion Whether a new business object data can be created.
     * @param servletRequest the servlet request.
     *
     * @return the S3 key prefix
     */
    @RequestMapping(
        value = "/businessObjectData/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages" +
            "/{businessObjectFormatUsage}/businessObjectFormatFileTypes/{businessObjectFormatFileType}" +
            "/businessObjectFormatVersions/{businessObjectFormatVersion}/s3KeyPrefix",
        method = RequestMethod.GET)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_S3_KEY_PREFIX_GET)
    public S3KeyPrefixInformation getS3KeyPrefix(@PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion,
        @RequestParam(value = "partitionKey", required = false) String partitionKey, @RequestParam("partitionValue") String partitionValue,
        @RequestParam(value = "subPartitionValues", required = false) DelimitedFieldValues subPartitionValues,
        @RequestParam(value = "businessObjectDataVersion", required = false) Integer businessObjectDataVersion,
        @RequestParam(value = "createNewVersion", required = false, defaultValue = "false") Boolean createNewVersion, ServletRequest servletRequest)
    {
        return businessObjectDataService.getS3KeyPrefix(
            validateRequestAndCreateBusinessObjectDataKey(null, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, partitionValue, subPartitionValues, businessObjectDataVersion, servletRequest), partitionKey, createNewVersion);
    }

    /**
     * <p> Gets the S3 key prefix for writing or accessing business object data. </p> <p> This endpoint requires a namespace. </p>
     *
     * @param namespace the namespace.
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionKey the partition key.
     * @param partitionValue the partition value.
     * @param subPartitionValues the sub-partition values.
     * @param businessObjectDataVersion the business object data version.
     * @param createNewVersion Whether a new business object data can be created.
     * @param servletRequest the servlet request.
     *
     * @return the S3 key prefix
     */
    @RequestMapping(
        value = "/businessObjectData/namespaces/{namespace}/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages" +
            "/{businessObjectFormatUsage}/businessObjectFormatFileTypes/{businessObjectFormatFileType}" +
            "/businessObjectFormatVersions/{businessObjectFormatVersion}/s3KeyPrefix",
        method = RequestMethod.GET)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_S3_KEY_PREFIX_GET)
    public S3KeyPrefixInformation getS3KeyPrefix(@PathVariable("namespace") String namespace,
        @PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion,
        @RequestParam(value = "partitionKey", required = false) String partitionKey, @RequestParam("partitionValue") String partitionValue,
        @RequestParam(value = "subPartitionValues", required = false) DelimitedFieldValues subPartitionValues,
        @RequestParam(value = "businessObjectDataVersion", required = false) Integer businessObjectDataVersion,
        @RequestParam(value = "createNewVersion", required = false, defaultValue = "false") Boolean createNewVersion, ServletRequest servletRequest)
    {
        return businessObjectDataService.getS3KeyPrefix(
            validateRequestAndCreateBusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, partitionValue, subPartitionValues, businessObjectDataVersion, servletRequest), partitionKey, createNewVersion);
    }

    /**
     * Creates (i.e. registers) business object data.
     *
     * @param businessObjectDataCreateRequest the information needed to create the business object data.
     *
     * @return the created business object data.
     */
    @RequestMapping(value = "/businessObjectData", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_POST)
    public BusinessObjectData createBusinessObjectData(@RequestBody BusinessObjectDataCreateRequest businessObjectDataCreateRequest)
    {
        BusinessObjectData businessObjectData = businessObjectDataService.createBusinessObjectData(businessObjectDataCreateRequest);

        // TODO This should be enhanced such that the notification events are captured by probably as an advice, and these calls are not specified everywhere
        // in the code.

        // The calls to notifications is being done in REST layer so that the event transaction (e.g. in this case: create business object data) is committed 
        // and the event data is available for when notification is processed.

        // With proposed designed, when we go to event publish mode(e.g. create a database record for the event that will be picked up by notification 
        // processing engine), We would want the event transaction to also rollback if event publishing failed. These calls will be moved to service layer.

        // Trigger notifications.
        BusinessObjectDataKey businessObjectDataKey = dmHelper.getBusinessObjectDataKey(businessObjectData);

        // Create business object data notification.
        notificationEventService
            .processBusinessObjectDataNotificationEventAsync(NotificationEventTypeEntity.EVENT_TYPES_BDATA.BUS_OBJCT_DATA_RGSTN, businessObjectDataKey);

        return businessObjectData;
    }

    /**
     * Retrieves existing business object data entry information.
     *
     * @return the retrieved business object data information
     */
    /**
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatPartitionKey the business object format partition key.
     * @param businessObjectDataPartitionValue the business object data partition value.
     * @param subPartitionValues the sub-partition values.
     * @param businessObjectFormatVersion the business object format version.
     * @param businessObjectDataVersion the business object data version.
     *
     * @return the business object data.
     */
    @RequestMapping(
        value = "/businessObjectData/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}",
        method = RequestMethod.GET)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_GET)
    public BusinessObjectData getBusinessObjectData(@PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @RequestParam(value = "partitionKey", required = false) String businessObjectFormatPartitionKey,
        @RequestParam("partitionValue") String businessObjectDataPartitionValue,
        @RequestParam(value = "subPartitionValues", required = false) DelimitedFieldValues subPartitionValues,
        @RequestParam(value = "businessObjectFormatVersion", required = false) Integer businessObjectFormatVersion,
        @RequestParam(value = "businessObjectDataVersion", required = false) Integer businessObjectDataVersion)
    {
        return businessObjectDataService.getBusinessObjectData(
            new BusinessObjectDataKey(null, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType, businessObjectFormatVersion,
                businessObjectDataPartitionValue, subPartitionValues == null ? new ArrayList<String>() : subPartitionValues.getValues(),
                businessObjectDataVersion), businessObjectFormatPartitionKey);
    }

    /**
     *
     */
    /**
     * Retrieves existing business object data entry information with namespace.
     *
     * @param namespace the namespace.
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatPartitionKey the partition key.
     * @param businessObjectDataPartitionValue the partition value.
     * @param subPartitionValues the sub-partition values.
     * @param businessObjectFormatVersion the business object format version.
     * @param businessObjectDataVersion the business object data version.
     *
     * @return the retrieved business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/namespaces/{namespace}" +
            "/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}",
        method = RequestMethod.GET)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_GET)
    public BusinessObjectData getBusinessObjectData(@PathVariable("namespace") String namespace,
        @PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @RequestParam(value = "partitionKey", required = false) String businessObjectFormatPartitionKey,
        @RequestParam("partitionValue") String businessObjectDataPartitionValue,
        @RequestParam(value = "subPartitionValues", required = false) DelimitedFieldValues subPartitionValues,
        @RequestParam(value = "businessObjectFormatVersion", required = false) Integer businessObjectFormatVersion,
        @RequestParam(value = "businessObjectDataVersion", required = false) Integer businessObjectDataVersion)
    {
        return businessObjectDataService.getBusinessObjectData(
            new BusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, businessObjectDataPartitionValue,
                subPartitionValues == null ? new ArrayList<String>() : subPartitionValues.getValues(), businessObjectDataVersion),
            businessObjectFormatPartitionKey);
    }

    /**
     * Retrieves a list of existing business object data versions.
     *
     * @param namespace the namespace.
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectDataPartitionValue the partition value.
     * @param subPartitionValues the sub-partition values.
     * @param businessObjectFormatVersion the business object format version.
     * @param businessObjectDataVersion the business object data version.
     *
     * @return the retrieved business object data versions
     */
    @RequestMapping(
        value = "/businessObjectData/namespaces/{namespace}" +
            "/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/versions",
        method = RequestMethod.GET)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_VERSIONS_GET)
    public BusinessObjectDataVersions getBusinessObjectDataVersions(@PathVariable("namespace") String namespace,
        @PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @RequestParam("partitionValue") String businessObjectDataPartitionValue,
        @RequestParam(value = "subPartitionValues", required = false) DelimitedFieldValues subPartitionValues,
        @RequestParam(value = "businessObjectFormatVersion", required = false) Integer businessObjectFormatVersion,
        @RequestParam(value = "businessObjectDataVersion", required = false) Integer businessObjectDataVersion)
    {
        return businessObjectDataService.getBusinessObjectDataVersions(
            new BusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, businessObjectDataPartitionValue,
                subPartitionValues == null ? new ArrayList<String>() : subPartitionValues.getValues(), businessObjectDataVersion));
    }

    /**
     *
     */
    /**
     * Deletes an existing business object data without subpartition values.
     *
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion, @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(null, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType, businessObjectFormatVersion,
                partitionValue, new ArrayList<String>(), businessObjectDataVersion), deleteFiles);
    }

    /**
     * Deletes an existing business object data with 1 subpartition value.
     *
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param subPartition1Value the sub-partition value.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/subPartition1Values/{subPartition1Value}/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("subPartition1Value") String subPartition1Value, @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion,
        @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(null, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType, businessObjectFormatVersion,
                partitionValue, Arrays.asList(subPartition1Value), businessObjectDataVersion), deleteFiles);
    }

    /**
     * Deletes an existing business object data with 2 subpartition values.
     *
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param subPartition1Value sub-partition value 1.
     * @param subPartition2Value sub-partition value 2.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/subPartition1Values/{subPartition1Value}/subPartition2Values/{subPartition2Value}" +
            "/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("subPartition1Value") String subPartition1Value, @PathVariable("subPartition2Value") String subPartition2Value,
        @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion, @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(null, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType, businessObjectFormatVersion,
                partitionValue, Arrays.asList(subPartition1Value, subPartition2Value), businessObjectDataVersion), deleteFiles);
    }

    /**
     * Deletes an existing business object data with 3 subpartition values.
     *
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param subPartition1Value sub-partition value 1.
     * @param subPartition2Value sub-partition value 2.
     * @param subPartition3Value sub-partition value 3.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/subPartition1Values/{subPartition1Value}/subPartition2Values/{subPartition2Value}" +
            "/subPartition3Values/{subPartition3Value}/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("subPartition1Value") String subPartition1Value, @PathVariable("subPartition2Value") String subPartition2Value,
        @PathVariable("subPartition3Value") String subPartition3Value, @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion,
        @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(null, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType, businessObjectFormatVersion,
                partitionValue, Arrays.asList(subPartition1Value, subPartition2Value, subPartition3Value), businessObjectDataVersion), deleteFiles);
    }

    /**
     * Deletes an existing business object data with 4 subpartition values.
     *
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param subPartition1Value sub-partition value 1.
     * @param subPartition2Value sub-partition value 2.
     * @param subPartition3Value sub-partition value 3.
     * @param subPartition4Value sub-partition value 4.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/subPartition1Values/{subPartition1Value}/subPartition2Values/{subPartition2Value}" +
            "/subPartition3Values/{subPartition3Value}/subPartition4Values/{subPartition4Value}/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("subPartition1Value") String subPartition1Value, @PathVariable("subPartition2Value") String subPartition2Value,
        @PathVariable("subPartition3Value") String subPartition3Value, @PathVariable("subPartition4Value") String subPartition4Value,
        @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion, @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(null, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType, businessObjectFormatVersion,
                partitionValue, Arrays.asList(subPartition1Value, subPartition2Value, subPartition3Value, subPartition4Value), businessObjectDataVersion),
            deleteFiles);
    }

    /**
     *
     */
    /**
     * Deletes an existing business object data without subpartition values with namespace.
     *
     * @param namespace the namespace.
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/namespaces/{namespace}" +
            "/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("namespace") String namespace,
        @PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion, @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, partitionValue, new ArrayList<String>(), businessObjectDataVersion), deleteFiles);
    }

    /**
     * Deletes an existing business object data with 1 subpartition value with namespace.
     *
     * @param namespace the namespace.
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param subPartition1Value sub-partition value 1.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/namespaces/{namespace}" +
            "/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/subPartition1Values/{subPartition1Value}/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("namespace") String namespace,
        @PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("subPartition1Value") String subPartition1Value, @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion,
        @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, partitionValue, Arrays.asList(subPartition1Value), businessObjectDataVersion), deleteFiles);
    }

    /**
     * Deletes an existing business object data with 2 subpartition values with namespace.
     *
     * @param namespace the namespace.
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param subPartition1Value sub-partition value 1.
     * @param subPartition2Value sub-partition value 2.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/namespaces/{namespace}" +
            "/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/subPartition1Values/{subPartition1Value}/subPartition2Values/{subPartition2Value}" +
            "/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("namespace") String namespace,
        @PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("subPartition1Value") String subPartition1Value, @PathVariable("subPartition2Value") String subPartition2Value,
        @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion, @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, partitionValue, Arrays.asList(subPartition1Value, subPartition2Value), businessObjectDataVersion), deleteFiles);
    }

    /**
     * Deletes an existing business object data with 3 subpartition values with namespace.
     *
     * @param namespace the namespace.
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param subPartition1Value sub-partition value 1.
     * @param subPartition2Value sub-partition value 2.
     * @param subPartition3Value sub-partition value 3.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/namespaces/{namespace}" +
            "/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/subPartition1Values/{subPartition1Value}/subPartition2Values/{subPartition2Value}" +
            "/subPartition3Values/{subPartition3Value}/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("namespace") String namespace,
        @PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("subPartition1Value") String subPartition1Value, @PathVariable("subPartition2Value") String subPartition2Value,
        @PathVariable("subPartition3Value") String subPartition3Value, @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion,
        @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, partitionValue, Arrays.asList(subPartition1Value, subPartition2Value, subPartition3Value),
                businessObjectDataVersion), deleteFiles);
    }

    /**
     * Deletes an existing business object data with 4 subpartition values with namespace.
     *
     * @param namespace the namespace.
     * @param businessObjectDefinitionName the business object definition name.
     * @param businessObjectFormatUsage the business object format usage.
     * @param businessObjectFormatFileType the business object format file type.
     * @param businessObjectFormatVersion the business object format version.
     * @param partitionValue the partition value.
     * @param subPartition1Value sub-partition value 1.
     * @param subPartition2Value sub-partition value 2.
     * @param subPartition3Value sub-partition value 3.
     * @param subPartition4Value sub-partition value 4.
     * @param businessObjectDataVersion the business object data version.
     * @param deleteFiles whether files should be deleted.
     *
     * @return the deleted business object data information
     */
    @RequestMapping(
        value = "/businessObjectData/namespaces/{namespace}" +
            "/businessObjectDefinitionNames/{businessObjectDefinitionName}/businessObjectFormatUsages/{businessObjectFormatUsage}" +
            "/businessObjectFormatFileTypes/{businessObjectFormatFileType}/businessObjectFormatVersions/{businessObjectFormatVersion}" +
            "/partitionValues/{partitionValue}/subPartition1Values/{subPartition1Value}/subPartition2Values/{subPartition2Value}" +
            "/subPartition3Values/{subPartition3Value}/subPartition4Values/{subPartition4Value}/businessObjectDataVersions/{businessObjectDataVersion}",
        method = RequestMethod.DELETE)
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_DELETE)
    public BusinessObjectData deleteBusinessObjectData(@PathVariable("namespace") String namespace,
        @PathVariable("businessObjectDefinitionName") String businessObjectDefinitionName,
        @PathVariable("businessObjectFormatUsage") String businessObjectFormatUsage,
        @PathVariable("businessObjectFormatFileType") String businessObjectFormatFileType,
        @PathVariable("businessObjectFormatVersion") Integer businessObjectFormatVersion, @PathVariable("partitionValue") String partitionValue,
        @PathVariable("subPartition1Value") String subPartition1Value, @PathVariable("subPartition2Value") String subPartition2Value,
        @PathVariable("subPartition3Value") String subPartition3Value, @PathVariable("subPartition4Value") String subPartition4Value,
        @PathVariable("businessObjectDataVersion") Integer businessObjectDataVersion, @RequestParam("deleteFiles") Boolean deleteFiles)
    {
        return businessObjectDataService.deleteBusinessObjectData(
            new BusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
                businessObjectFormatVersion, partitionValue, Arrays.asList(subPartition1Value, subPartition2Value, subPartition3Value, subPartition4Value),
                businessObjectDataVersion), deleteFiles);
    }

    /**
     * Performs a search and returns a list of business object data key values and relative statuses for a range of requested business object data.
     *
     * @param businessObjectDataAvailabilityRequest the business object data availability request
     *
     * @return the business object data availability information
     */
    @RequestMapping(value = "/businessObjectData/availability", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_AVAILABILITY_POST)
    public BusinessObjectDataAvailability checkBusinessObjectDataAvailability(
        @RequestBody BusinessObjectDataAvailabilityRequest businessObjectDataAvailabilityRequest)
    {
        return businessObjectDataService.checkBusinessObjectDataAvailability(businessObjectDataAvailabilityRequest);
    }

    /**
     * Performs an availability check for a collection of business object data.
     *
     * @param businessObjectDataAvailabilityCollectionRequest the business object data availability collection request
     *
     * @return the business object data availability information
     */
    @RequestMapping(value = "/businessObjectData/availabilityCollection", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_AVAILABILITY_COLLECTION_POST)
    public BusinessObjectDataAvailabilityCollectionResponse checkBusinessObjectDataAvailabilityCollection(
        @RequestBody BusinessObjectDataAvailabilityCollectionRequest businessObjectDataAvailabilityCollectionRequest)
    {
        return businessObjectDataService.checkBusinessObjectDataAvailabilityCollection(businessObjectDataAvailabilityCollectionRequest);
    }

    /**
     * Retrieves the DDL to initialize the specified type of the database system to perform queries for a range of requested business object data in the
     * specified storage.
     *
     * @param businessObjectDataDdlRequest the business object data DDL request
     *
     * @return the business object data DDL information
     */
    @RequestMapping(value = "/businessObjectData/generateDdl", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_GENERATE_DDL_POST)
    public BusinessObjectDataDdl generateBusinessObjectDataDdl(@RequestBody BusinessObjectDataDdlRequest businessObjectDataDdlRequest)
    {
        return businessObjectDataService.generateBusinessObjectDataDdl(businessObjectDataDdlRequest);
    }

    /**
     * Retrieves the DDL to initialize the specified type of the database system to perform queries for a collection of business object data in the specified
     * storage.
     *
     * @param businessObjectDataDdlCollectionRequest the business object data DDL collection request
     *
     * @return the business object data DDL information
     */
    @RequestMapping(value = "/businessObjectData/generateDdlCollection", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_GENERATE_DDL_COLLECTION_POST)
    public BusinessObjectDataDdlCollectionResponse generateBusinessObjectDataDdlCollection(
        @RequestBody BusinessObjectDataDdlCollectionRequest businessObjectDataDdlCollectionRequest)
    {
        return businessObjectDataService.generateBusinessObjectDataDdlCollection(businessObjectDataDdlCollectionRequest);
    }

    /**
     * Registers data as INVALID for objects which exist in S3 but are not registered in DM.
     *
     * @param businessObjectDataInvalidateUnregisteredRequest {@link BusinessObjectDataInvalidateUnregisteredRequest}
     *
     * @return {@link BusinessObjectDataInvalidateUnregisteredResponse}
     */
    @RequestMapping(value = "/businessObjectData/unregistered/invalidation", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
    @Secured(SecurityFunctions.FN_BUSINESS_OBJECT_DATA_UNREGISTERED_INVALIDATE)
    public BusinessObjectDataInvalidateUnregisteredResponse invalidateUnregisteredBusinessObjectData(
        @RequestBody BusinessObjectDataInvalidateUnregisteredRequest businessObjectDataInvalidateUnregisteredRequest)
    {
        return businessObjectDataService.invalidateUnregisteredBusinessObjectData(businessObjectDataInvalidateUnregisteredRequest);
    }

    /**
     * Validates the given {@code servletRequest} and constructs a new {@link BusinessObjectDataKey}. The {@code servletRequest} validation involves validations
     * of request parameters which Spring MVC may not implement out-of-the-box. In our case, the request is asserted to no contain duplicate parameters.
     *
     * @param namespace the namespace
     * @param businessObjectDefinitionName the business object definition name
     * @param businessObjectFormatUsage the business object format usage
     * @param businessObjectFormatFileType the business object format type
     * @param businessObjectFormatVersion the business object format version
     * @param partitionValue the partition value
     * @param subPartitionValues the list of sub-partition values
     * @param businessObjectDataVersion the business object data version
     * @param servletRequest the servlet request
     *
     * @return a new {@link BusinessObjectDataKey}
     */
    private BusinessObjectDataKey validateRequestAndCreateBusinessObjectDataKey(String namespace, String businessObjectDefinitionName,
        String businessObjectFormatUsage, String businessObjectFormatFileType, Integer businessObjectFormatVersion, String partitionValue,
        DelimitedFieldValues subPartitionValues, Integer businessObjectDataVersion, ServletRequest servletRequest)
    {
        // Ensure there are no duplicate query string parameters.
        dmHelper.validateNoDuplicateQueryStringParams(servletRequest.getParameterMap(), "partitionKey", "partitionValue");

        // Invoke the service.
        return new BusinessObjectDataKey(namespace, businessObjectDefinitionName, businessObjectFormatUsage, businessObjectFormatFileType,
            businessObjectFormatVersion, partitionValue, subPartitionValues == null ? new ArrayList<String>() : subPartitionValues.getValues(),
            businessObjectDataVersion);
    }
}
