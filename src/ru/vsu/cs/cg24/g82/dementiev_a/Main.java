package ru.vsu.cs.cg24.g82.dementiev_a;

import ru.vsu.cs.cg24.g82.dementiev_a.model.Model;
import ru.vsu.cs.cg24.g82.dementiev_a.obj_reader.ObjReader;
import ru.vsu.cs.cg24.g82.dementiev_a.obj_writer.ObjWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите путь к файлу модели (в папке models/before/):");
        String filename = scanner.nextLine();

        try {
            Model model = read("before/" + filename);

            System.out.println("Введите номера вершин для удаления (через пробел, начиная с 0):");
            String input = scanner.nextLine();
            ArrayList<Integer> vertexIndicesToRemove = parseVertexIndices(input, model.vertices.size());

            model.removeVertices(vertexIndicesToRemove);

            ObjWriter objWriter = new ObjWriter();
            objWriter.write(model, "models/after/" + filename);
            System.out.println("Модель сохранена в models/after/");

        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Ошибка преобразования числа: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Некорректный аргумент: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static ArrayList<Integer> parseVertexIndices(String input, int maxIndex) {
        ArrayList<Integer> indices = new ArrayList<>();
        Arrays.stream(input.split("\\s+"))
                .mapToInt(Integer::parseInt)
                .filter(i -> i >= 0 && i < maxIndex)
                .forEach(indices::add);

        if (indices.isEmpty()) {
            throw new IllegalArgumentException("Не указаны вершины для удаления");
        }
        return indices;
    }


    private static Model read(String filename) throws IOException {
        Path fileName = Path.of("models/" + filename);
        String fileContent = Files.readString(fileName);
        System.out.println("Загрузка модели " + filename);
        return ObjReader.read(fileContent);
    }
}