package de.tu_berlin.dima.niteout.routing.model;

/**
 * An address in Germany
 */
public class Address {

    private String houseNumber;
    private String street;
    private String city;
    private int postalCode;

    private Address(AddressBuilder addressBuilder) {
        this.houseNumber = addressBuilder.houseNumber;
        this.street = addressBuilder.street;
        this.city = addressBuilder.city;
        this.postalCode = addressBuilder.postalCode;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return String.format("%05d", postalCode);
    }

    public static class AddressBuilder {
        private String houseNumber;
        private String street;
        private String city;
        private int postalCode;

        public AddressBuilder houseNumber(String houseNumber) {
            this.houseNumber = houseNumber.trim();
            return this;
        }

        public AddressBuilder street(String street) {
            this.street = street.trim();
            return this;
        }

        public AddressBuilder city(String city) {
            this.street = street.trim();
            return this;
        }

        public AddressBuilder postalCode(int postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Address build() {

            if (postalCode < 0) {
                throw new IllegalArgumentException("postalCode cannot be a negative number");
            }
            if (postalCode > 99999) {
                throw new IllegalArgumentException("postalCode cannot be more than 5 digits long");
            }

            return new Address(this);
        }
    }
}
