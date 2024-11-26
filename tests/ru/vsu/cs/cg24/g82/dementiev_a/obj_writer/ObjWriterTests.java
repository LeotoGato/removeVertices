package ru.vsu.cs.cg24.g82.dementiev_a.obj_writer;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.vsu.cs.cg24.g82.dementiev_a.math.Vector2f;
import ru.vsu.cs.cg24.g82.dementiev_a.math.Vector3f;
import ru.vsu.cs.cg24.g82.dementiev_a.model.Model;
import ru.vsu.cs.cg24.g82.dementiev_a.model.Polygon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ObjWriterTests {
    private final ObjWriter objWriter = new ObjWriter();
    //Vector3f в OBJ
    @ParameterizedTest
    @CsvSource({ "2.3f, -4.54f, 0", "0, 0, 0", "132.2652624646f, 0.0131f, -5.5437f", "-2.8f, -0.0, 9.3" })
    public void vertexToStringTest(float x, float y, float z) {
        String result = objWriter.vertexToString(new Vector3f(x, y, z));
        String[] array = result.split(" ");
        Assertions.assertEquals("v", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
        Assertions.assertEquals(z, Float.parseFloat(array[3]));
    }
//Vector2f в obj
    @ParameterizedTest
    @CsvSource({ "2.3f, -4.54f", "0, 0", "132.2652624646f, 0.0131f", "-2.8f, -0.0" })
    public void textureVertexToStringTest(float x, float y) {
        String result = objWriter.textureVertexToString(new Vector2f(x, y));
        String[] array = result.split(" ");
        Assertions.assertEquals("vt", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
    }
//нормаль в obj
    @ParameterizedTest
    @CsvSource({ "2.3f, -4.54f, 0", "0, 0, 0", "132.2652624646f, 0.0131f, -5.5437f", "-2.8f, -0.0, 9.3" })
    public void normalToStringTest(float x, float y, float z) {
        String result = objWriter.normalToString(new Vector3f(x, y, z));
        String[] array = result.split(" ");
        Assertions.assertEquals("vn", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
        Assertions.assertEquals(z, Float.parseFloat(array[3]));
    }
//полигон в obj, когда индексы вершин
    @Test
    public void polygonToStringTestWithOnlyVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        String result = objWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1 2 3", result);
    }
    //полигон в obj, когда индексы вершин и текстурных вершин
    @Test
    public void polygonToStringTestWithTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 5)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(3, 5, 4, 2)));
        String result = objWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1/4 2/6 3/5 6/3", result);
    }
    //polygon в obj, когда заданы индексы вершин и нормалей
    @Test
    public void polygonToStringTestWithNormalIndicesAndWithoutTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 5)));
        polygon.setNormalIndices(new ArrayList<>(List.of(3, 5, 4, 2)));
        String result = objWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1//4 2//6 3//5 6//3", result);
    }
//poligon в obj, когда заданы индексы вершин, индексы нормалей и текстурных вершин
    @Test
    public void polygonToStringTestWithNormalIndicesAndWithTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 5)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(7, 4, 3, 6)));
        polygon.setNormalIndices(new ArrayList<>(List.of(3, 5, 4, 2)));
        String result = objWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1/8/4 2/5/6 3/4/5 6/7/3", result);
    }
    //Проверка, что при удалении вершины, на которую
    // не ссылается ни один полигон, удаление происходит корректно
    @Test
    public void testRemoveVertexWithNoReferencingPolygons() {
        Model model = new Model();
        model.vertices = new ArrayList<>(List.of(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1),
                new Vector3f(2, 2, 2),
                new Vector3f(3, 3, 3)
        ));
        model.textureVertices = new ArrayList<>(List.of(
                new Vector2f(0, 0),
                new Vector2f(1, 1),
                new Vector2f(2, 2),
                new Vector2f(3, 3)
        ));
        model.normals = new ArrayList<>(List.of(
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 1, 1)
        ));
        Polygon polygon1 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon1.setTextureVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon1.setNormalIndices(new ArrayList<>(List.of(0, 1, 2)));
        Polygon polygon2 = new Polygon();
        polygon2.setVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        polygon2.setTextureVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        polygon2.setNormalIndices(new ArrayList<>(List.of(1, 2, 3)));
        model.polygons = new ArrayList<>(List.of(polygon1, polygon2));
        model.removeVertices(new ArrayList<>(List.of(0)));
        Assertions.assertEquals(3, model.vertices.size());
        Assertions.assertEquals(3, model.textureVertices.size());
        Assertions.assertEquals(3, model.normals.size());
        Assertions.assertEquals(2, model.polygons.size());
        Assertions.assertEquals(new Vector3f(1, 1, 1), model.vertices.get(0));
        Assertions.assertEquals(new Vector2f(1, 1), model.textureVertices.get(0));
        Assertions.assertEquals(new Vector3f(0, 1, 0), model.normals.get(0));
        Polygon newPolygon1 = model.polygons.get(0);
        Assertions.assertEquals(new ArrayList<>(List.of(0, 1, 2)), newPolygon1.getVertexIndices());
        Assertions.assertEquals(new ArrayList<>(List.of(0, 1, 2)), newPolygon1.getTextureVertexIndices());
        Assertions.assertEquals(new ArrayList<>(List.of(0, 1, 2)), newPolygon1.getNormalIndices());
        Polygon newPolygon2 = model.polygons.get(1);
        Assertions.assertEquals(new ArrayList<>(List.of(0, 1, 2)), newPolygon2.getVertexIndices());
        Assertions.assertEquals(new ArrayList<>(List.of(1, 2, 3)), newPolygon2.getTextureVertexIndices());
        Assertions.assertEquals(new ArrayList<>(List.of(1, 2, 3)), newPolygon2.getNormalIndices());
    }
    //Тест проверяет, что при удалении вершины, на которую ссылается
    //один или более полигонов, удаление происходит корректно
    @Test
    public void testRemoveVertexReferencedByPolygons() {
        Model model = new Model();
        model.vertices = new ArrayList<>(List.of(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1),
                new Vector3f(2, 2, 2),
                new Vector3f(3, 3, 3)
        ));
        model.textureVertices = new ArrayList<>(List.of(
                new Vector2f(0, 0),
                new Vector2f(1, 1),
                new Vector2f(2, 2),
                new Vector2f(3, 3)
        ));
        model.normals = new ArrayList<>(List.of(
                new Vector3f(0, 0, 1),
                new Vector3f(0, 1, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 1, 1)
        ));
        Polygon polygon1 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon1.setTextureVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        polygon1.setNormalIndices(new ArrayList<>(List.of(0, 1, 2)));
        Polygon polygon2 = new Polygon();
        polygon2.setVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        polygon2.setTextureVertexIndices(new ArrayList<>(List.of(1, 2, 3)));
        polygon2.setNormalIndices(new ArrayList<>(List.of(1, 2, 3)));
        model.polygons = new ArrayList<>(List.of(polygon1, polygon2));
        model.removeVertices(new ArrayList<>(List.of(1)));
        Assertions.assertEquals(3, model.vertices.size());
        Assertions.assertEquals(3, model.textureVertices.size());
        Assertions.assertEquals(3, model.normals.size());
        Assertions.assertEquals(1, model.polygons.size());
        Assertions.assertEquals(new Vector3f(0, 0, 0), model.vertices.get(0));
        Assertions.assertEquals(new Vector2f(0, 0), model.textureVertices.get(0));
        Assertions.assertEquals(new Vector3f(0, 0, 1), model.normals.get(0));
        Polygon newPolygon = model.polygons.get(0);
        Assertions.assertEquals(new ArrayList<>(List.of(0, 1, 2)), newPolygon.getVertexIndices());
        Assertions.assertEquals(new ArrayList<>(List.of(0, 1, 2)), newPolygon.getTextureVertexIndices());
        Assertions.assertEquals(new ArrayList<>(List.of(0, 1, 2)), newPolygon.getNormalIndices());
    }
    //проверка записи модели
    @ParameterizedTest
    @ValueSource(strings = {"test.obj", "test", "тест.obj", "анонимная модель.obj", "../папка/ещё папка/моделька.obj", "../../meow.obj"})
    public void writeTest(String testFilename) throws IOException {
        Model model = new Model();
        model.vertices = new ArrayList<>(List.of(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1.2f, 3.6f),
                new Vector3f(-2, -4.45f, 7f),
                new Vector3f(-1.5f, -4.45f, 6.5f),
                new Vector3f(10f, 11f, 0)
        ));
        Polygon polygon1 = new Polygon();
        Polygon polygon2 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 3)));
        polygon2.setVertexIndices(new ArrayList<>(List.of(2, 4, 3)));
        model.polygons = new ArrayList<>(List.of(
                polygon1, polygon2
        ));
        objWriter.write(model, testFilename);
        Path path = Path.of(testFilename);
        String separator = System.lineSeparator();
        File file = path.toFile();
        String content = Files.readString(path, StandardCharsets.UTF_8);
        Assertions.assertEquals(
                "v 0.0 0.0 0.0" + separator +
                        "v 1.0 1.2 3.6" + separator +
                        "v -2.0 -4.45 7.0" + separator +
                        "v -1.5 -4.45 6.5" + separator +
                        "v 10.0 11.0 0.0" + separator +
                        "f 1 2 4" + separator +
                        "f 3 5 4" + separator,
                content
        );
        int depth = testFilename.split(File.pathSeparator).length - 1;
        if (depth == 0) depth = 1;
        while (depth > 0 && file.delete()) {
            file = file.getParentFile();
            depth--;
        }
    }
}
