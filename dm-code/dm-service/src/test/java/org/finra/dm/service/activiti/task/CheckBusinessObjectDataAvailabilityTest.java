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
import org.finra.dm.service.activiti.ActivitiHelper;

/**
 * Tests the CheckBusinessObjectDataAvailability Activiti task wrapper.
 */
public class CheckBusinessObjectDataAvailabilityTest extends DmActivitiServiceTaskTest
{
    /**
     * This method tests the availability activiti task with partition values
     */
    @Test
    public void testAvailabilityTaskWithPartitionValues() throws Exception
    {
        createDatabaseEntitiesForBusinessObjectDataAvailabilityTesting(null);

        List<FieldExtension> fieldExtensionList = new ArrayList<>(getMandatoryFields());

        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        fieldExtensionList.add(buildFieldExtension("partitionKey", "${partitionKey}"));
        fieldExtensionList.add(buildFieldExtension("partitionValues", "${partitionValues}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDataVersion", "${businessObjectDataVersion}"));

        List<Parameter> parameters = new ArrayList<>(getMandatoryParameters());

        parameters.add(buildParameter("businessObjectFormatVersion", FORMAT_VERSION.toString()));
        parameters.add(buildParameter("partitionKey", FIRST_PARTITION_COLUMN_NAME));
        parameters.add(buildParameter("partitionValues", dmHelper.buildStringWithDefaultDelimiter(PARTITION_VALUES_AVAILABLE)));
        parameters.add(buildParameter("businessObjectDataVersion", DATA_VERSION.toString()));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(CheckBusinessObjectDataAvailability.VARIABLE_IS_ALL_DATA_AVAILABLE, Boolean.TRUE);
        testActivitiServiceTaskSuccess(CheckBusinessObjectDataAvailability.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    @Test
    public void testAvailabilityTaskWithPartitionValuesLegacy() throws Exception
    {
        // Create a legacy business object definition.
        createBusinessObjectDefinitionEntity(NAMESPACE_CD, BOD_NAME, DATA_PROVIDER_NAME, BOD_DESCRIPTION, true);

        createDatabaseEntitiesForBusinessObjectDataAvailabilityTesting(null);

        List<FieldExtension> fieldExtensionList = new ArrayList<>();

        fieldExtensionList.add(buildFieldExtension("businessObjectDefinitionName", "${businessObjectDefinitionName}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatUsage", "${businessObjectFormatUsage}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatFileType", "${businessObjectFormatFileType}"));
        fieldExtensionList.add(buildFieldExtension("storageName", "${storageName}"));

        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));
        fieldExtensionList.add(buildFieldExtension("partitionKey", "${partitionKey}"));
        fieldExtensionList.add(buildFieldExtension("partitionValues", "${partitionValues}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDataVersion", "${businessObjectDataVersion}"));

        List<Parameter> parameters = new ArrayList<>();

        parameters.add(buildParameter("businessObjectDefinitionName", BOD_NAME));
        parameters.add(buildParameter("businessObjectFormatUsage", FORMAT_USAGE_CODE));
        parameters.add(buildParameter("businessObjectFormatFileType", FORMAT_FILE_TYPE_CODE));
        parameters.add(buildParameter("storageName", STORAGE_NAME));

        parameters.add(buildParameter("businessObjectFormatVersion", FORMAT_VERSION.toString()));
        parameters.add(buildParameter("partitionKey", FIRST_PARTITION_COLUMN_NAME));
        parameters.add(buildParameter("partitionValues", dmHelper.buildStringWithDefaultDelimiter(PARTITION_VALUES_AVAILABLE)));
        parameters.add(buildParameter("businessObjectDataVersion", DATA_VERSION.toString()));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(CheckBusinessObjectDataAvailability.VARIABLE_IS_ALL_DATA_AVAILABLE, Boolean.TRUE);
        testActivitiServiceTaskSuccess(CheckBusinessObjectDataAvailability.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * This method tests the availability activiti task with partition range
     */
    @Test
    public void testAvailabilityTaskWithPartitionRange() throws Exception
    {
        createDatabaseEntitiesForBusinessObjectDataAvailabilityTesting(PARTITION_KEY_GROUP);
        createExpectedPartitionValueProcessDatesForApril2014(PARTITION_KEY_GROUP);

        List<FieldExtension> fieldExtensionList = new ArrayList<>(getMandatoryFields());

        fieldExtensionList.add(buildFieldExtension("startPartitionValue", "${startPartitionValue}"));
        fieldExtensionList.add(buildFieldExtension("endPartitionValue", "${endPartitionValue}"));

        List<Parameter> parameters = new ArrayList<>(getMandatoryParameters());

        parameters.add(buildParameter("startPartitionValue", START_PARTITION_VALUE));
        parameters.add(buildParameter("endPartitionValue", END_PARTITION_VALUE));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(CheckBusinessObjectDataAvailability.VARIABLE_IS_ALL_DATA_AVAILABLE, Boolean.FALSE);
        testActivitiServiceTaskSuccess(CheckBusinessObjectDataAvailability.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * This method tests the invalid values for format version
     */
    @Test
    public void testAvailabilityTaskWithInvalidFormatVersion() throws Exception
    {
        List<FieldExtension> fieldExtensionList = new ArrayList<>();

        fieldExtensionList.add(buildFieldExtension("businessObjectFormatVersion", "${businessObjectFormatVersion}"));

        List<Parameter> parameters = new ArrayList<>();

        parameters.add(buildParameter("businessObjectFormatVersion", "invalid_integer"));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(ActivitiHelper.VARIABLE_ERROR_MESSAGE, "\"BusinessObjectFormatVersion\" must be a valid integer value.");

        testActivitiServiceTaskFailure(CheckBusinessObjectDataAvailability.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * This method tests the invalid values for data version
     */
    @Test
    public void testAvailabilityTaskWithInvalidDataVersion() throws Exception
    {
        List<FieldExtension> fieldExtensionList = new ArrayList<>();

        fieldExtensionList.add(buildFieldExtension("businessObjectDataVersion", "${businessObjectDataVersion}"));

        List<Parameter> parameters = new ArrayList<>();

        parameters.add(buildParameter("businessObjectDataVersion", "invalid_integer"));

        Map<String, Object> variableValuesToValidate = new HashMap<>();
        variableValuesToValidate.put(ActivitiHelper.VARIABLE_ERROR_MESSAGE, "\"BusinessObjectDataVersion\" must be a valid integer value.");

        testActivitiServiceTaskFailure(CheckBusinessObjectDataAvailability.class.getCanonicalName(), fieldExtensionList, parameters, variableValuesToValidate);
    }

    /**
     * Gets the mandatory fields for task
     *
     * @return List<FieldExtension>, mandatory fields
     */
    private List<FieldExtension> getMandatoryFields()
    {
        List<FieldExtension> fieldExtensionList = new ArrayList<>();

        fieldExtensionList.add(buildFieldExtension("namespace", "${namespace}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectDefinitionName", "${businessObjectDefinitionName}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatUsage", "${businessObjectFormatUsage}"));
        fieldExtensionList.add(buildFieldExtension("businessObjectFormatFileType", "${businessObjectFormatFileType}"));
        fieldExtensionList.add(buildFieldExtension("storageName", "${storageName}"));

        return fieldExtensionList;
    }

    /**
     * Gets the mandatory parameters for task
     *
     * @return List<Parameter>, parameters
     */
    private List<Parameter> getMandatoryParameters()
    {
        List<Parameter> parameters = new ArrayList<>();

        parameters.add(buildParameter("namespace", NAMESPACE_CD));
        parameters.add(buildParameter("businessObjectDefinitionName", BOD_NAME));
        parameters.add(buildParameter("businessObjectFormatUsage", FORMAT_USAGE_CODE));
        parameters.add(buildParameter("businessObjectFormatFileType", FORMAT_FILE_TYPE_CODE));
        parameters.add(buildParameter("storageName", STORAGE_NAME));

        return parameters;
    }
}