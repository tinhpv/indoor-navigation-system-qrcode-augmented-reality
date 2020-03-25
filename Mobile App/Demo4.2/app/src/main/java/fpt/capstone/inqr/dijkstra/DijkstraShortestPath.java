package fpt.capstone.inqr.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstraShortestPath {

    public void computeShortestPaths(Vertex sourceVertex) {

        sourceVertex.setDistance(0);
        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(sourceVertex);
        sourceVertex.setVisited(true);

        while (!priorityQueue.isEmpty()) {

            Vertex actualVertex = priorityQueue.poll();

            for (Edge edge : actualVertex.getAdjacenciesList()) {

                Vertex v = edge.getEndVertex();
                if (!v.isVisited()) {
                    double newDistance = actualVertex.getDistance() + edge.getWeight();

                    if (newDistance < v.getDistance()) {
                        priorityQueue.remove(v);
                        v.setDistance(newDistance);
                        v.setPredecessor(actualVertex);
                        priorityQueue.add(v);
                    }
                }
            }
            actualVertex.setVisited(true);
        }
    }

//    public double computeShortestPaths(Vertex sourceVertex) {
//        double distance = 0;
//        sourceVertex.setDistance(0);
//        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>();
//        priorityQueue.add(sourceVertex);
//        sourceVertex.setVisited(true);
//
//        while (!priorityQueue.isEmpty()) {
//
//            Vertex actualVertex = priorityQueue.poll();
//
//            for (Edge edge : actualVertex.getAdjacenciesList()) {
//
//                Vertex v = edge.getEndVertex();
//                if (!v.isVisited()) {
//                    double newDistance = actualVertex.getDistance() + edge.getWeight();
//
//                    if (newDistance < v.getDistance()) {
//                        priorityQueue.remove(v);
//                        v.setDistance(newDistance);
//                        v.setPredecessor(actualVertex);
//                        priorityQueue.add(v);
//                        distance += v.getDistance();
//                    }
//                }
//            }
//            actualVertex.setVisited(true);
//        }
//
//        return distance;
//    }

    public List<Vertex> getShortestsPathTo(Vertex targetVertex) {
        List<Vertex> path = new ArrayList<>();

        for (Vertex vertex = targetVertex; vertex != null; vertex = vertex.getPredecessor()) {
            path.add(vertex);
        }

        Collections.reverse(path);
        return path;
    }
}
