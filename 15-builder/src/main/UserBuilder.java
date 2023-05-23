package main;

public class UserBuilder {
    private final String firstName; // required
    private final String lastName; // required
    private int age; // optional
    private String phone; // optional
    private String address; // optional

    public UserBuilder(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
    }
}
