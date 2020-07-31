package nio.lesson02;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MainApp {

    // Рассмотрены библиотечные интерфейсы Path и Files;

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("./common","2","3","3.txt");
//        Path path2 = Paths.get("C:/source/cloud_storage/common/2/3/3.txt");
//        System.out.println(Files.exists(path)); // true
//        Path path3 = Paths.get("C:/source/cloud_storage/common/2/3/../3/3.txt"); // true, //.. - возврат к предыдущей папке
//        System.out.println(Files.exists(path3)); // true
//        System.out.println(path.getFileName()); // возвращает сам файл
//        System.out.println(path.getParent()); // возвращает полный путь
//        System.out.println(path.getName(0)); // вернет имя папки, находящейся на 1 месте(common)
//        System.out.println(path.getName(0)); // вернет имя папки, находящейся на 0 месте(.)
//        System.out.println(path.getNameCount()); //возвращает количество элементов в пути
//        System.out.println(path.getName(path.getNameCount()-1)); //возвращает имя последнего элемента, 3.txt
//        System.out.println(path3.normalize()); // выводит нормализует путь ,убирая переходы назад-вперед(/..)
//        System.out.println(path.isAbsolute()); // проверка на абсолютность пути
//        System.out.println(path.toAbsolutePath()); // преобразует относительный путь в абсолютный
        File file =path.toFile(); // мост между библиотеками io и nio, приводит path к file
        System.out.println(file + " :путь JavaIO");
        Path path1 = file.toPath(); // мост между библиотеками nio и io, приводит file к path
        System.out.println(path1  + " :путь JavaNIO");
//        Path path3 = Paths.get("./common","2","3");
//        path3.resolve("4/4.txt"); // достраивает путь, но не записывает..

        // Files.write()
//        byte[] data = "Привет".getBytes(StandardCharsets.UTF_8);
//        Files.write(Paths.get("./common","2","3","3.txt"), data); //3.txt теперь содержит текст Привет
//        byte[] data2 = "Java".getBytes();
//        Files.write(Paths.get("./common","2","3","4.txt"), data2); // создаст файл в случае его отсутствия и запишет в него
        // либо..
//        Files.write(Paths.get("./common","2","3","4.txt"), data2, StandardOpenOption.CREATE); // создаст файл и запишет в него, в случае его наличия перезапишет содержимое файла
//        Files.write(Paths.get("./common","2","3","5.txt"), data2, StandardOpenOption.CREATE_NEW); //вызовет эесепшн при наличии файла или создат новый в случае отсуттвия данного файла
//        Files.write(Paths.get("./common","2","3","4.txt"), data2, StandardOpenOption.APPEND); // открывает фай ли дописывает к имеющейся информации новую

        //Files.readAllBytes() - РЕКОМЕНДОВАНО ИСПОЛЬЗОВАТЬ ТОЛЬКО ЕСЛИ ФАЙЛЫ НЕБОЛЬШИЕ
//        String str = new String(Files.readAllBytes(Paths.get("./common","2","3","4.txt")));
//        System.out.println(str);
//
//        byte[] in = Files.readAllBytes(Paths.get("./common","2","3","4.txt"));
//        for (int i = 0; i < in.length ; i++) {
//            System.out.print((char) in[i]);
//        }

        //Files.walkFileTree() - обход по каталогу и нахождение всех файлов
        Files.walkFileTree(Paths.get("./common/2"), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file.getFileName());
                //return super.visitFile(file, attrs);
                return FileVisitResult.CONTINUE;
            }
        });

        //Files.lines()
        System.out.println(Files.lines(path)); // возвращает ссылку на файл
        System.out.println(Files.readAllLines(path)); // читает все строки в файле
        System.out.println(Files.readAllLines(path));

        //Files.list() - возвращает список файлов в директории не разобрался как работает
        Files.list(Paths.get("./common/2"));

        //Files.copy()
        Files.copy(Paths.get("./common","2","3","3.txt"),Paths.get("./common","2","3","3-copy.txt"), StandardCopyOption.REPLACE_EXISTING);

        // можно производить копию в файл либо копировать файлы и отправлять их в байтовом потоке в OutputStream
        //Files.copy(Paths.get("./common","2","3","3.txt"), Files.newOutputStream(Paths.get("./"), StandardOpenOption.CREATE));

        //Files.move() - аналогичный методу copy()
        Files.move(Paths.get("./common","2","3","3.txt"), Paths.get("./common","2",  "3-copy.txt"),StandardCopyOption.REPLACE_EXISTING);

        //создание директории Files.createDirectories() и Files.createDirectory()
        //Files.createDirectory(); - не знаю как работает

        //Создание файла
        //Files.createFile(); - не знаю как работает

        // Создание BufferReader и BufferWriter из файла
        Files.newBufferedReader(path);
        Files.newBufferedWriter(path, StandardOpenOption.CREATE);


    }
}
