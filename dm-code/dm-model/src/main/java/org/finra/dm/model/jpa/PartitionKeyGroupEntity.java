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
package org.finra.dm.model.jpa;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A partition key group.
 */
@XmlRootElement
@XmlType
@Table(name = PartitionKeyGroupEntity.TABLE_NAME)
@Entity
public class PartitionKeyGroupEntity extends AuditableEntity
{
    /**
     * The table name.
     */
    public static final String TABLE_NAME = "prtn_key_group";

    /**
     * The partition key group column.
     */
    @Id
    @Column(name = "prtn_key_group_tx")
    private String partitionKeyGroupName;

    @OneToMany(mappedBy = "partitionKeyGroup", orphanRemoval = true, cascade = {CascadeType.ALL})
    @OrderBy("partitionValue")
    private Collection<ExpectedPartitionValueEntity> expectedPartitionValues;

    public void setPartitionKeyGroupName(String partitionKeyGroupName)
    {
        this.partitionKeyGroupName = partitionKeyGroupName;
    }

    public String getPartitionKeyGroupName()
    {
        return partitionKeyGroupName;
    }

    public Collection<ExpectedPartitionValueEntity> getExpectedPartitionValues()
    {
        return expectedPartitionValues;
    }

    public void setExpectedPartitionValues(Collection<ExpectedPartitionValueEntity> expectedPartitionValues)
    {
        this.expectedPartitionValues = expectedPartitionValues;
    }

    /**
     * Indicates whether some other object is "equal to" this one. The two partition key group entities are equal if they have identical partition key group
     * values.
     *
     * @param o the other object
     *
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        PartitionKeyGroupEntity that = (PartitionKeyGroupEntity) o;

        if (!partitionKeyGroupName.equals(that.partitionKeyGroupName))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return partitionKeyGroupName.hashCode();
    }
}
