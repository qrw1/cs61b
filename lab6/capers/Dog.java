package capers;

import java.io.File;
import java.io.Serializable;
import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author TODO
*/
public class Dog implements Serializable{ // TODO

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = join(".capers","dogs"); // TODO (hint: look at the `join`
                                                                         //      function in Utils)

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        // TODO (hint: look at the Utils file)
        File dogFile = join(DOG_FOLDER, name);
        return readObject(dogFile, Dog.class);
    }
    //File dogFile = join(DOG_FOLDER, name);
    // 这行代码是在构建文件路径。通过调用 join 方法，将 DOG_FOLDER 和 name 这两
    // 个路径字符串连接起来，从而得到了表示特定狗对象的文件对象 dogFile。这个文
    // 件对象可以用来操作或者*查找*对应的文件。

    //readObject 方法会根据传入的文件路径 dogFile，读取该文件并尝试将其反序列化成一
    //个 Dog 对象。这个文件中保存的内容应该是之前通过 saveDog() 方法保存的 Dog
    // 对象的实例。因此，readObject 方法的作用是从指定路径的文件中读取 Dog 对象的实例，并返回这个对象。

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        // TODO (hint: don't forget dog names are unique)
        File filetosave = join(DOG_FOLDER, name);
        writeObject(filetosave,this);
    }
    //在 saveDog() 方法中，文件被保存到 filetosave 变量表示的位置。filetosave 是通过调用 join(DOG_FOLDER, name)
    // 方法得到的，其中 DOG_FOLDER 是狗对象存储的文件夹路径，name 是狗对象的名称，这两者连接起来构成了文件的路径。
    // 因此，文件将会被保存到 DOG_FOLDER 文件夹中，并且文件名是狗对象的名称。

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}
