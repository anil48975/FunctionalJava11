package _2_intersection_types.samples;

import java.io.*;

public class PersonWithGenerics
{
    public static void main(String[] args) throws IOException
    {
        RandomAccessFile randomAccess = new RandomAccessFile("src/person", "rw");
        DataInputStream stream = new DataInputStream(new FileInputStream("src/person"));

        PersonWithGenerics person = read(randomAccess);
        System.out.println(person);

        person = read(stream);
        System.out.println(person);
    }

    private static <I extends DataInput & Closeable> PersonWithGenerics read(I source)
    {
        try(I input = source)
        {
            return new PersonWithGenerics(input.readUTF(), input.readInt());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public PersonWithGenerics(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    private final String name;
    private final int age;

    public int getAge()
    {
        return age;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "Person{" +
            "name='" + name + '\'' +
            ", age=" + age +
            '}';
    }
}
