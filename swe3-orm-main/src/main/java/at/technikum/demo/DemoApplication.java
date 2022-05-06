package at.technikum.demo;

import at.technikum.demo.model.*;
import at.technikum.orm.ConnectionFactory;
import at.technikum.orm.Orm;
import at.technikum.orm.model.EntityValidationException;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
public class DemoApplication {
    public static void main(String[] args) throws SQLException {
        ConnectionFactory connectionFactory = ConnectionFactory.with("jdbc:mariadb://localhost:3306/swe3?user=root&password=root");


        Orm orm = new Orm(connectionFactory);

        orm.truncateTable(Author.class);
        orm.truncateTable(Book.class);
        orm.truncateTable(Borrow.class);
        orm.truncateTable(Student.class);
        orm.truncateTable(Teacher.class);
        orm.truncateTable(Type.class);

        // Demonstrate insert and fetch
        saveAndFetchTeacher(orm);
        // demonstrate insert and delete
        saveAndDeleteTeacher(orm);
        // demonstrate the update functionality
        updateAndFetchTeacher(orm);
        // use different entities
        saveAndFetchStudent(orm);
        //using a more complex entity
        saveAndFetchBook(orm);
        // using one-to-many relations
        saveAndFetchBorrows(orm);
        // shows that entities without an id field are not supported 
        handleInvalidEntity(orm);

    }

    private static void saveAndFetchTeacher(Orm orm) throws SQLException {
        log.info("Storing teacher");
        Teacher teacher = teacher();
        orm.persist(teacher);
        log.info("Stored teacher {}", teacher);
        Teacher fetchedTeacher = orm.get(Teacher.class, teacher.getId());
        log.info("Fetched teacher: {}", fetchedTeacher);
        fetchedTeacher.setFirstName("firstname 2");
        fetchedTeacher.setSalary(55555);
        orm.persist(fetchedTeacher);
        Teacher fetchedTeacher2 = orm.get(Teacher.class, teacher.getId());
        log.info("Fetched teacher: {}", fetchedTeacher2);
    }

    private static void saveAndDeleteTeacher(Orm orm) throws SQLException {
        log.info("Storing teacher");
        Teacher teacher = teacher();
        orm.persist(teacher);
        orm.delete(teacher);
        Teacher deletedTeacher = orm.get(Teacher.class, teacher.getId());
        log.info("Fetched teacher: {}", deletedTeacher);

    }

    private static void handleInvalidEntity(Orm orm) {
        try {
            InvalidEntity invalidEntity = new InvalidEntity();
            orm.persist(invalidEntity);
            throw new RuntimeException("This should not be reached");
        } catch (EntityValidationException e) {
            log.info("successfully validated Entity ID checker");
        }
    }

    private static void saveAndFetchStudent(Orm orm) throws SQLException {
        Student student = student();
        orm.persist(student);
        log.info("Stored student {}", student);
        Student fetchedStudent = orm.get(Student.class, student.getId());
        log.info("Fetched student: {}", fetchedStudent);
    }

    private static void saveAndFetchBook(Orm orm) throws SQLException {
        Author author = new Author();
        author.setFirstName("Agatha");
        author.setLastName("Christie");
        orm.persist(author);
        log.info("Stored author {}", author);

        Type type = new Type();
        type.setName("Crime");
        orm.persist(type);
        log.info("Stored type {}", type);

        Book book = new Book();
        book.setTitel("And Then There Were None");
        book.setType(type);
        book.setAuthor(author);
        log.info("Storing book");
        orm.persist(book);
        log.info("Stored book {}", book);
        Book fetchedBook = orm.get(Book.class, book.getB_id());
        log.info("Fetched book: {}", fetchedBook);

    }

    private static void saveAndFetchBorrows(Orm orm) throws SQLException {
        Author author = new Author();
        author.setFirstName("Agatha");
        author.setLastName("Christie");
        orm.persist(author);
        Type type = new Type();
        type.setName("Crime");
        orm.persist(type);
        log.info("Stored type {}", type);


        Book book = new Book();
        book.setAuthor(author);
        book.setType(type);
        book.setTitel("Title of the Book");
        orm.persist(book);
        log.info("Persisted book: {}", book);

        Teacher teacher = teacher();
        orm.persist(teacher);
        log.info("created teacher: {}", teacher);
        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setPerson(teacher);
        orm.persist(borrow);
        log.info("Stored borrow {}", borrow);
        Borrow fetchedBorrow = orm.get(Borrow.class, borrow.getBorrow_id());
        log.info("Fetched borrow: {}", fetchedBorrow);
    }

    private static void updateAndFetchTeacher(Orm orm) throws SQLException {

        Teacher teacher = teacher();
        orm.persist(teacher);
        log.info("created teacher: {}", teacher);
        teacher.setSalary(456);
        orm.persist(teacher);
        log.info("Updated teacher: {}", teacher);
        teacher = orm.get(Teacher.class, teacher.getId());
        log.info("Fetched teacher: {}", teacher);

    }

    private static Teacher teacher() {
        Teacher teacher = new Teacher();
        LocalDate enterDate = LocalDate.of(1970, 1, 1);
        LocalDate leaveDate = LocalDate.of(2021, 4, 17);
        teacher.setSalary(123);
        teacher.setGender(Gender.DIVERSE);
        teacher.setFirstName("Burcu");
        teacher.setLastName("Baumgartner");
        teacher.setEnterDate(enterDate);
        teacher.setLeaveDate(leaveDate);
        return teacher;
    }

    private static Student student() {
        Student student = new Student();
        LocalDate enterDate = LocalDate.of(2021, 1, 1);
        LocalDate leaveDate = LocalDate.of(2023, 4, 17);
        student.setFirstName("Jane");
        student.setLastName("Doe");
        student.setGender(Gender.FEMALE);
        student.setGPA("1.2");
        student.setEnterDate(enterDate);
        student.setLeaveDate(leaveDate);
        return student;
    }


}
