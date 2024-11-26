package ru.vsu.cs.cg24.g82.dementiev_a.model;

import ru.vsu.cs.cg24.g82.dementiev_a.math.Vector2f;
import ru.vsu.cs.cg24.g82.dementiev_a.math.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<>();
    public ArrayList<Vector3f> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();

    public void removeVertices(ArrayList<Integer> vertexIndicesToRemove) {
        ArrayList<Integer> newVertexIndices = new ArrayList<>();
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        // Создаем mapping старых индексов на новые
        ArrayList<Integer> indexMapping = new ArrayList<>();
        int newIndexCounter = 0;
        for (int i = 0; i < vertices.size(); i++) {
            if (!vertexIndicesToRemove.contains(i)) {
                indexMapping.add(newIndexCounter++);
            } else {
                indexMapping.add(-1); // Маркируем удаляемые вершины
            }
        }

        for (Polygon polygon : polygons) {
            boolean removePolygon = false;
            ArrayList<Integer> newPolygonVertexIndices = new ArrayList<>();
            ArrayList<Integer> newTextureIndices = new ArrayList<>();
            ArrayList<Integer> newNormalIndices = new ArrayList<>();

            List<Integer> vertexIndices = polygon.getVertexIndices();
            List<Integer> textureIndices = polygon.getTextureVertexIndices();
            List<Integer> normalIndices = polygon.getNormalIndices();


            // Проверка границ индексов для каждой из составляющих полигона
            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i);
                if (vertexIndex >= 0 && vertexIndex < indexMapping.size()) {
                    int newIndex = indexMapping.get(vertexIndex);
                    if (newIndex == -1) {
                        removePolygon = true;
                        break;
                    }
                    newPolygonVertexIndices.add(newIndex);
                } else {
                    removePolygon = true;
                    break;
                }
            }

            //Проверка границ для текстурных координат
            if (!removePolygon && textureIndices.size() > 0) {
                for (int i = 0; i < textureIndices.size(); i++) {
                    int textureIndex = textureIndices.get(i);
                    if (textureIndex >= 0 && textureIndex < indexMapping.size()) {
                        int newIndex = indexMapping.get(textureIndex);
                        if (newIndex == -1) {
                            removePolygon = true;
                            break;
                        }
                        newTextureIndices.add(newIndex);
                    } else {
                        removePolygon = true;
                        break;
                    }
                }
            }

            // Проверка границ для нормалей
            if (!removePolygon && normalIndices.size() > 0) {
                for (int i = 0; i < normalIndices.size(); i++) {
                    int normalIndex = normalIndices.get(i);
                    if (normalIndex >= 0 && normalIndex < indexMapping.size()) {
                        int newIndex = indexMapping.get(normalIndex);
                        if (newIndex == -1) {
                            removePolygon = true;
                            break;
                        }
                        newNormalIndices.add(newIndex);
                    } else {
                        removePolygon = true;
                        break;
                    }
                }
            }

            if (!removePolygon) {
                Polygon newPolygon = new Polygon();
                newPolygon.setVertexIndices(newPolygonVertexIndices);
                newPolygon.setTextureVertexIndices(newTextureIndices);
                newPolygon.setNormalIndices(newNormalIndices);
                newPolygons.add(newPolygon);
            }
        }

        // Создаем новые списки вершин, текстурных координат и нормалей
        ArrayList<Vector3f> newVertices = new ArrayList<>();
        ArrayList<Vector2f> newTextureVertices = new ArrayList<>();
        ArrayList<Vector3f> newNormals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (!vertexIndicesToRemove.contains(i)) {
                newVertices.add(vertices.get(i));
                if (textureVertices.size() > i) {
                    newTextureVertices.add(textureVertices.get(i));
                }
                if (normals.size() > i) {
                    newNormals.add(normals.get(i));
                }
            }
        }

        this.vertices = newVertices;
        this.textureVertices = newTextureVertices;
        this.normals = newNormals;
        this.polygons = newPolygons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model model)) return false;
        return Objects.equals(vertices, model.vertices) && Objects.equals(textureVertices, model.textureVertices) && Objects.equals(normals, model.normals) && Objects.equals(polygons, model.polygons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, textureVertices, normals, polygons);
    }
}