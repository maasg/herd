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
package org.finra.dm.service.activiti.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.FieldExtension;
import org.junit.Test;

import org.finra.dm.model.api.xml.Parameter;
import org.finra.dm.model.api.xml.SchemaColumn;
import org.finra.dm.service.activiti.ActivitiHelper;

/**
 * Test suite for Get S3 Key Prefix Activiti wrapper.
 */
public class GetS3KeyPrefixTest extends DmActivitiServiceTaskTest
{
    /**
     * This unit tests passes all required and optional parameters.  Please note that since we specify business object data version in the request, the create
     * new version flag has no impact on the outcome.  For that reason, we have an extra unit test cases added - please see {@link
     * #testGetS3KeyPrefixInitialDataVersionExists() testGetS3KeyPrefixInitialDataVersionExists} unit test.
     */
    @Test
    public void testGetS3KeyPrefix() throws Exception
    {
        List<SchemaColumn> columns = getTestSchemaColumns("Test_COLUMN", SCHEMA_COLUMNS);
        List<SchemaColumn> partitionColumns = columns.subList(0, 5);
        List<SchemaColumn> subPartitionColumns = partitionColumns.subList(1, 5);
        String partitionKey = partitionColumns.get(0).getName();

        // Create a business object format entity.
        createBusinessObjectFormatEntity(NAMESPACE_CD, BOD_NAME, FORMAT_USAGE_CODE, FORMAT_FILE_TYPE_CODE, FORMAT_VERSION, FORMAT_DESCRIPTION, true,
            partitionKey, null, SCHEMA_DELIMITER_PIPE, SCHEMA_ESCAPE_CHARACTER_BACKSLASH, SCHEMA_NULL_VALUE_BACKSLASH_N, columns, partitionColumns);

        List<FieldExtension> fieldExtensionList = new ArrayList<>();

        fieldExtensionList.add(buildFieldExtension("namespace", "${namespace}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDefinitionName", "${businessObjectDefinitionName}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatUsage", "${businessObjectFormatUsage}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatFileType", "${businessObjectFormatFileType}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        fieldExtensionList.add(buildFieldExtension("partitionKey", "${partitionKey}"));
        fieldExtensionList.add(buildFieldExtension("partitionValue", "${partitionValue}"));
        fieldExtensionList.add(buildFieldExtension("subPartitionValues", "${subPartitionValues}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDataVersion", "${businessObjectDataVersion}"));
        fieldExtensionList.add(buildFieldExtension("createNewVersion", "${createNewVersion}"));

        List<Parameter> parameters = new ArrayList<>();

        parameters.add(buildParameter("namespace", NAMESPACE_CD));
        parameters.add(buildParameter("businessObjectDefinitionName", BOD_NAME));
        parameters.add(buildParameter("businessObjectFormatUsage", FORMAT_USAGE_CODE));
        parameters.add(buildParameter("businessObjectFormatFileType", FORMAT_FILE_TYPE_CODE));
        parameters.add(buildParameter("businessObjectFormatVersion", FORMAT_VERSION.toString()));
        parameters.add(buildParameter("partitionKey", partitionKey));
        parameters.add(buildParameter("partitionValue", PARTITION_VALUE));
        parameters.add(buildParameter("subPartitionValues", dmHelper.buildStringWithDefaultDelimiter(SUBPARTITION_VALUES)));
        parameters.add(buildParameter("businessObjectDataVersion", DATA_VERSION.toString()));
        parameters.add(buildParameter("createNewVersion", "false"));

        // Get the expected S3 key prefix value.
        String expectedS3KeyPrefix =
            getExpectedS3KeyPrefix(NAMESPACE_CD, DATA_PROVIDER_NAME, BOD_NAME, FORMAT_USAGE_CODE, FORMAT_FILE_TYPE_CODE, FORMAT_VERSION, partitionKey,
                PARTITION_VALUE, subPartitionColumns.toArray(new SchemaColumn[subPartitionColumns.size()]),
                SUBPARTITION_VALUES.toArray(new String[SUBPARTITION_VALUES.size()]), DATA_VERSION);

        // Run the activiti task and validate the returned S3 key prefix value.
        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(GetS3KeyPrefix.VARIABLE_S3_KEY_PREFIX, expectedS3KeyPrefix);
        testActivitiServiceTaskSuccess(GetS3KeyPrefix.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * This unit tests covers scenario when business object data service fails due to a missing required parameter.
     */
    @Test
    public void testGetS3KeyPrefixMissingBusinessObjectDefinitionName() throws Exception
    {
        // Validate that activiti task fails when we do not pass a business object definition name value.
        List<FieldExtension> fieldExtensionList = new ArrayList<>();
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDataVersion", "${businessObjectDataVersion}"));
        fieldExtensionList.add(buildFieldExtension("createNewVersion", "${createNewVersion}"));
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(buildParameter("businessObjectFormatVersion", FORMAT_VERSION.toString()));
        parameters.add(buildParameter("businessObjectDataVersion", DATA_VERSION.toString()));
        parameters.add(buildParameter("createNewVersion", "false"));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(ActivitiHelper.VARIABLE_ERROR_MESSAGE, "A business object definition name must be specified.");

        testActivitiServiceTaskFailure(GetS3KeyPrefix.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * This unit tests validates the required parameter checks done in the activiti service layer.
     */
    @Test
    public void testGetS3KeyPrefixMissingBusinessObjectFormatVersion() throws Exception
    {
        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(ActivitiHelper.VARIABLE_ERROR_MESSAGE, "\"businessObjectFormatVersion\" must be specified.");

        // Validate that activiti task fails when we do not pass a business object format version value.
        testActivitiServiceTaskFailure(GetS3KeyPrefix.class.getCanonicalName(), new ArrayList<FieldExtension>(), new ArrayList<Parameter>(),
            variableValuesToValidate);
    }

    /**
     * This unit test validates that we can get S3 key prefix when we do not pass any of the optional parameters. Thus, the unit tests calls the activiti task
     * to retrieve an S3 key prefix for the initial version of a business object data to be registered without subpartition values and for a business object
     * format with a legacy business object definition name.
     */
    @Test
    public void testGetS3KeyPrefixMissingOptionalParameters() throws Exception
    {
        // Create a legacy business object definition entity.
        createBusinessObjectDefinitionEntity(NAMESPACE_CD, BOD_NAME, DATA_PROVIDER_NAME, BOD_DESCRIPTION, true);

        // Create a business object format entity without a schema.
        createBusinessObjectFormatEntity(NAMESPACE_CD, BOD_NAME, FORMAT_USAGE_CODE, FORMAT_FILE_TYPE_CODE, FORMAT_VERSION, FORMAT_DESCRIPTION, true,
            PARTITION_KEY);

        List<FieldExtension> fieldExtensionList = new ArrayList<>();

        fieldExtensionList.add(buildFieldExtension("businessObjectDefinitionName", "${businessObjectDefinitionName}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatUsage", "${businessObjectFormatUsage}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatFileType", "${businessObjectFormatFileType}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        fieldExtensionList.add(buildFieldExtension("partitionValue", "${partitionValue}"));

        List<Parameter> parameters = new ArrayList<>();

        parameters.add(buildParameter("businessObjectDefinitionName", BOD_NAME));
        parameters.add(buildParameter("businessObjectFormatUsage", FORMAT_USAGE_CODE));
        parameters.add(buildParameter("businessObjectFormatFileType", FORMAT_FILE_TYPE_CODE));
        parameters.add(buildParameter("businessObjectFormatVersion", FORMAT_VERSION.toString()));
        parameters.add(buildParameter("partitionValue", PARTITION_VALUE));

        // Get the expected S3 key prefix value.
        String expectedS3KeyPrefix =
            getExpectedS3KeyPrefix(NAMESPACE_CD, DATA_PROVIDER_NAME, BOD_NAME, FORMAT_USAGE_CODE, FORMAT_FILE_TYPE_CODE, FORMAT_VERSION, PARTITION_KEY,
                PARTITION_VALUE, null, null, INITIAL_DATA_VERSION);

        // Run the activiti task and validate the returned S3 key prefix value.
        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(GetS3KeyPrefix.VARIABLE_S3_KEY_PREFIX, expectedS3KeyPrefix);
        testActivitiServiceTaskSuccess(GetS3KeyPrefix.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * This unit tests validates that activiti service layer fails when we pass non-integer business object format version value.
     */
    @Test
    public void testGetS3KeyPrefixInvalidBusinessObjectFormatVersion() throws Exception
    {
        // Validate that activiti task fails when we pass non-integer value for a business object format version.
        List<FieldExtension> fieldExtensionList = new ArrayList<>();
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(buildParameter("businessObjectFormatVersion", "NOT_AN_INTEGER"));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(ActivitiHelper.VARIABLE_ERROR_MESSAGE, "\"businessObjectFormatVersion\" must be a valid integer value.");

        testActivitiServiceTaskFailure(GetS3KeyPrefix.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * This unit tests validates that activiti service layer fails when we pass non-integer business object data version value.
     */
    @Test
    public void testGetS3KeyPrefixInvalidBusinessObjectDataVersion() throws Exception
    {
        // Validate that activiti task fails when we pass non-integer value for a business object data version.
        List<FieldExtension> fieldExtensionList = new ArrayList<>();
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDataVersion", "${businessObjectDataVersion}"));
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(buildParameter("businessObjectFormatVersion", FORMAT_VERSION.toString()));
        parameters.add(buildParameter("businessObjectDataVersion", "NOT_AN_INTEGER"));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(ActivitiHelper.VARIABLE_ERROR_MESSAGE, "\"businessObjectDataVersion\" must be a valid integer value.");

        testActivitiServiceTaskFailure(GetS3KeyPrefix.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * This unit tests validates that activiti service layer fails when we pass non-boolean create new version flag value.
     */
    @Test
    public void testGetS3KeyPrefixInvalidCreateNewVersionFlag() throws Exception
    {
        // Validate that activiti task fails when we pass non-boolean value for create new version flag.
        List<FieldExtension> fieldExtensionList = new ArrayList<>();
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDataVersion", "${businessObjectDataVersion}"));
        fieldExtensionList.add(buildFieldExtension("createNewVersion", "${createNewVersion}"));
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(buildParameter("businessObjectFormatVersion", FORMAT_VERSION.toString()));
        parameters.add(buildParameter("businessObjectDataVersion", DATA_VERSION.toString()));
        parameters.add(buildParameter("createNewVersion", "NOT_A_BOOLEAN"));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(ActivitiHelper.VARIABLE_ERROR_MESSAGE, "\"createNewVersion\" must be a valid boolean value of \"true\" or \"false\".");

        testActivitiServiceTaskFailure(GetS3KeyPrefix.class.getCanonicalName(), fieldExtensionList, parameters, null);
    }

    /**
     * This test unit validates that we can get an S3 key prefix when initial business object data version already exists.  This test case is here just to
     * confirm that we are correctly passing the create new version flag value when calling the activiti task.
     */
    @Test
    public void testGetS3KeyPrefixInitialDataVersionExists() throws Exception
    {
        // Create an initial version of a business object data.
        createBusinessObjectDataEntity(NAMESPACE_CD, BOD_NAME, FORMAT_USAGE_CODE, FORMAT_FILE_TYPE_CODE, FORMAT_VERSION, PARTITION_VALUE, INITIAL_DATA_VERSION,
            true, BDATA_STATUS);

        List<FieldExtension> fieldExtensionList = new ArrayList<>();

        fieldExtensionList.add(buildFieldExtension("namespace", "${namespace}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDefinitionName", "${businessObjectDefinitionName}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatUsage", "${businessObjectFormatUsage}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatFileType", "${businessObjectFormatFileType}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        fieldExtensionList.add(buildFieldExtension("partitionKey", "${partitionKey}"));
        fieldExtensionList.add(buildFieldExtension("partitionValue", "${partitionValue}"));
        fieldExtensionList.add(buildFieldExtension("createNewVersion", "${createNewVersion}"));

        List<Parameter> parameters = new ArrayList<>();

        parameters.add(buildParameter("namespace", NAMESPACE_CD));
        parameters.add(buildParameter("businessObjectDefinitionName", BOD_NAME));
        parameters.add(buildParameter("businessObjectFormatUsage", FORMAT_USAGE_CODE));
        parameters.add(buildParameter("businessObjectFormatFileType", FORMAT_FILE_TYPE_CODE));
        parameters.add(buildParameter("businessObjectFormatVersion", FORMAT_VERSION.toString()));
        parameters.add(buildParameter("partitionKey", PARTITION_KEY));
        parameters.add(buildParameter("partitionValue", PARTITION_VALUE));
        parameters.add(buildParameter("createNewVersion", "true"));

        // Get the expected S3 key prefix value.
        String expectedS3KeyPrefix =
            getExpectedS3KeyPrefix(NAMESPACE_CD, DATA_PROVIDER_NAME, BOD_NAME, FORMAT_USAGE_CODE, FORMAT_FILE_TYPE_CODE, FORMAT_VERSION, PARTITION_KEY,
                PARTITION_VALUE, null, null, SECOND_DATA_VERSION);

        // Run the activiti task and validate the returned S3 key prefix value.
        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(GetS3KeyPrefix.VARIABLE_S3_KEY_PREFIX, expectedS3KeyPrefix);
        testActivitiServiceTaskSuccess(GetS3KeyPrefix.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }
}
