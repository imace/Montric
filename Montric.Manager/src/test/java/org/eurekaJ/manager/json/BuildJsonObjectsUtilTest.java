/**
    EurekaJ Profiler - http://eurekaj.haagen.name
    
    Copyright (C) 2010-2011 Joachim Haagen Skeie

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.eurekaJ.manager.json;

import java.util.ArrayList;
import java.util.List;

import org.eurekaj.api.datatypes.Alert;
import org.eurekaj.api.datatypes.GroupedStatistics;
import org.eurekaj.api.datatypes.Statistics;
import org.eurekaj.manager.datatypes.ManagerStatistics;
import org.eurekaj.manager.json.BuildJsonObjectsUtil;
import org.jsflot.xydata.XYDataList;
import org.jsflot.xydata.XYDataPoint;
import org.jsflot.xydata.XYDataSetCollection;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuildJsonObjectsUtilTest {

    @Test
    public void test_that_an_empty_array_creates_empty_json_object() throws JSONException {
        List<Statistics> emptyList = new ArrayList<Statistics>();

        JSONArray jsonObject = BuildJsonObjectsUtil.buildTreeTypeMenuJsonObject("treeMenuID", emptyList, new ArrayList<Alert>(), new ArrayList<GroupedStatistics>(), 0, 20, false, "chart");
        String actual = jsonObject.toString();
        assertEquals("[]", actual);
    }

    @Test
    public void test_that_one_node_element_returns_correct_json_object() throws JSONException {
        List<Statistics> nodeList = new ArrayList<Statistics>();
        nodeList.add(new ManagerStatistics("A", "ACCOUNT", "Y"));

        JSONArray jsonObject = BuildJsonObjectsUtil.buildTreeTypeMenuJsonObject("treeMenuID", nodeList, new ArrayList<Alert>(), new ArrayList<GroupedStatistics>(), 0, 20, false, "chart");
        assertEquals("[{\"id\":\"A\",\"name\":\"A\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"A\",\"parent_id\":null}]", jsonObject.toString());
        
        
    }

    @Test
    public void test_that_two_node_element_returns_correct_json_object() throws JSONException {
        List<Statistics> nodeList = new ArrayList<Statistics>();
        nodeList.add(new ManagerStatistics("A", "ACCOUNT", "Y"));
        nodeList.add(new ManagerStatistics("B", "ACCOUNT", "Y"));
        		
        JSONArray jsonObject = BuildJsonObjectsUtil.buildTreeTypeMenuJsonObject("treeMenuID", nodeList, new ArrayList<Alert>(), new ArrayList<GroupedStatistics>(), 0, 20, false, "chart");
        assertEquals("[{\"id\":\"A\",\"name\":\"A\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"A\",\"parent_id\":null},{\"id\":\"B\",\"name\":\"B\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"B\",\"parent_id\":null}]", jsonObject.toString());
    }

    @Test
    public void test_that_second_level_nodes_generated_correct_json() throws JSONException {
        List<Statistics> nodeList = new ArrayList<Statistics>();
        nodeList.add(new ManagerStatistics("A", "ACCOUNT", "Y"));
        nodeList.add(new ManagerStatistics("B", "ACCOUNT", "Y"));
        nodeList.add(new ManagerStatistics("A:C", "ACCOUNT", "Y"));

        JSONArray jsonObject = BuildJsonObjectsUtil.buildTreeTypeMenuJsonObject("treeMenuID", nodeList, new ArrayList<Alert>(), new ArrayList<GroupedStatistics>(),  0, 20, false, "chart");
        assertEquals("[{\"id\":\"A\",\"name\":\"A\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"A\",\"parent_id\":null},{\"id\":\"B\",\"name\":\"B\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"B\",\"parent_id\":null}]", jsonObject.toString());
    }

    @Test
    public void test_that_third_level_nodes_generated_correct_json() throws JSONException {
        List<Statistics> nodeList = new ArrayList<Statistics>();
        nodeList.add(new ManagerStatistics("A", "ACCOUNT", "Y"));
        nodeList.add(new ManagerStatistics("B", "ACCOUNT", "Y"));
        nodeList.add(new ManagerStatistics("A:C", "ACCOUNT", "Y"));
        nodeList.add(new ManagerStatistics("A:C:D", "ACCOUNT", "Y"));
        		
        StringBuilder expected = new StringBuilder();
        expected.append("[");
        expected.append("{\"id\":\"A\",\"name\":\"A\",\"children_ids\":[\"A:C\"],\"node_type\":\"chart\",\"chart_id\":\"A\",\"parent_id\":null}");
        expected.append(",");
        expected.append("{\"id\":\"A:C\",\"name\":\"C\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"A:C\",\"parent_id\":\"A\"}");
        expected.append(",");
        expected.append("{\"id\":\"B\",\"name\":\"B\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"B\",\"parent_id\":null}");

        //expected.append(",");
        //expected.append("{\"guid\":4,\"isSelected\":false,\"name\":\"D\",\"treeItemIsExpanded\":false,\"guiPath\":\"A:C:D\",\"parentPath\":\"A:C\"}");
        expected.append("]");

        JSONArray jsonObject = BuildJsonObjectsUtil.buildTreeTypeMenuJsonObject("treeMenuID", nodeList, new ArrayList<Alert>(), new ArrayList<GroupedStatistics>(),  0, 20, false, "chart");
        assertEquals(expected.toString(), jsonObject.toString());
    }

    @Test
    public void test_that_third_level_nodes_without_parents_are_generated_correct_json() throws JSONException {
        List<Statistics> nodeList = new ArrayList<Statistics>();
        nodeList.add(new ManagerStatistics("A:B:C", "ACCOUNT", "Y"));
        
        StringBuilder expected = new StringBuilder();
        expected.append("[");
        expected.append("{\"id\":\"A\",\"name\":\"A\",\"children_ids\":[\"A:B\"],\"node_type\":\"chart\",\"chart_id\":\"A\",\"parent_id\":null}");
        expected.append(",");
        expected.append("{\"id\":\"A:B\",\"name\":\"B\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"A:B\",\"parent_id\":\"A\"}");
        //expected.append(",");
        //expected.append("{\"guid\":3,\"isSelected\":false,\"name\":\"C\",\"treeItemIsExpanded\":false,\"guiPath\":\"A:B:C\",\"parentPath\":\"A:B\"}");
        expected.append("]");

        JSONArray jsonObject = BuildJsonObjectsUtil.buildTreeTypeMenuJsonObject("treeMenuID", nodeList, new ArrayList<Alert>(), new ArrayList<GroupedStatistics>(),  0, 20, false, "chart");
        assertEquals(expected.toString(), jsonObject.toString());
    }

    @Test
    public void test_that_deep_nodes_without_parents_generates_correct_json() throws JSONException {
        List<Statistics> nodeList = new ArrayList<Statistics>();
        nodeList.add(new ManagerStatistics("JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent:<init>:Max Selftime", "ACCOUNT", "Y"));
        
        StringBuilder expected = new StringBuilder();
        expected.append("[");
        expected.append("{\"id\":\"JSFlotJAgent\",\"name\":\"JSFlotJAgent\",\"children_ids\":[\"JSFlotJAgent:Custom\"],\"node_type\":\"chart\",\"chart_id\":\"JSFlotJAgent\",\"parent_id\":null}");
        expected.append(",");
        expected.append("{\"id\":\"JSFlotJAgent:Custom\",\"name\":\"Custom\",\"children_ids\":[\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent\"],\"node_type\":\"chart\",\"chart_id\":\"JSFlotJAgent:Custom\",\"parent_id\":\"JSFlotJAgent\"}");
        expected.append(",");
        expected.append("{\"id\":\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent\",\"name\":\"org.jsflot.components.BubbleDataPointComponent\",\"children_ids\":[\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent:<init>\"],\"node_type\":\"chart\",\"chart_id\":\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent\",\"parent_id\":\"JSFlotJAgent:Custom\"}");
        expected.append(",");
        expected.append("{\"id\":\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent:<init>\",\"name\":\"<init>\",\"children_ids\":[],\"node_type\":\"chart\",\"chart_id\":\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent:<init>\",\"parent_id\":\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent\"}");
        //expected.append(",");
        //expected.append("{\"guid\":\"\",\"isSelected\":false,\"name\":\"Max Selftime\",\"treeItemIsExpanded\":false,\"guiPath\":\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent:<init>:Max Selftime\",\"parentPath\":\"JSFlotJAgent:Custom:org.jsflot.components.BubbleDataPointComponent:<init>\"}");
        expected.append("]");

        JSONArray jsonObject = BuildJsonObjectsUtil.buildTreeTypeMenuJsonObject("treeMenuID", nodeList, new ArrayList<Alert>(), new ArrayList<GroupedStatistics>(),  0, 20, false, "chart");
        assertEquals(expected.toString(), jsonObject.toString());
    }

    @Test
    @Ignore
    public void test_that_XYDataCollection_returns_correct_json() throws JSONException {
        XYDataSetCollection xyCollection = new XYDataSetCollection();
        XYDataList xyList = new XYDataList();
        xyList.setLabel("Set1");
        xyList.addDataPoint(new XYDataPoint(1, 1));
        xyList.addDataPoint(new XYDataPoint(2, 2));
        xyList.addDataPoint(new XYDataPoint(3, 3));
        xyList.addDataPoint(new XYDataPoint(5, 5));
        xyCollection.addDataList(xyList);

        StringBuilder expected = new StringBuilder();
        expected.append("{\"chart\": [ {\"label\": \"Set1\", \"data\": [[1,1], [2,2], [3,3], [5,5]]}],\"instrumentationNode\":\"chartId\"}");
        String jsonString = BuildJsonObjectsUtil.generateChartData("chartId", "something", xyCollection, 0l);
        assertEquals(expected.toString(), jsonString);
    }
}
