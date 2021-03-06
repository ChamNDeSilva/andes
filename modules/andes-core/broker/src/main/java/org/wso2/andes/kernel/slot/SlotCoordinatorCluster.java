/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.andes.kernel.slot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.andes.server.cluster.coordination.hazelcast.HazelcastAgent;
import org.wso2.andes.thrift.MBThriftClient;

/**
 * This class is responsible of coordinating with the cluster mode Slot Manager
 */
public class SlotCoordinatorCluster implements SlotCoordinator {

    private static Log log = LogFactory.getLog(SlotDeliveryWorker.class);
    String nodeId;


    public SlotCoordinatorCluster(){
        nodeId = HazelcastAgent.getInstance().getNodeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Slot getSlot(String queueName) throws ConnectionException {
        return MBThriftClient.getSlot(queueName, nodeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateMessageId(String queueName,
                                long startMessageId, long endMessageId) throws ConnectionException {
        MBThriftClient.updateMessageId(queueName,nodeId,startMessageId,endMessageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSlotDeletionSafeZone(long currentSlotDeleteSafeZone) {
        MBThriftClient.updateSlotDeletionSafeZone(currentSlotDeleteSafeZone, nodeId);
        if(log.isDebugEnabled()) {
            log.info("Submitted safe zone from node : " + nodeId + " | safe zone : " +
                    currentSlotDeleteSafeZone);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteSlot(String queueName, Slot slot) throws ConnectionException {
        return MBThriftClient.deleteSlot(queueName, slot, nodeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reAssignSlotWhenNoSubscribers(String queueName) throws ConnectionException {
        MBThriftClient.reAssignSlotWhenNoSubscribers(nodeId, queueName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAllActiveSlotRelationsToQueue(String queueName) {
        SlotManagerClusterMode.getInstance().clearAllActiveSlotRelationsToQueue(queueName);
    }
}
