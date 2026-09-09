package com.fakestore.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de usuario mapeado desde https://fakestoreapi.com/users
 *
 * REGLA DE NEGOCIO CRÍTICA (ver sección 2 de las especificaciones):
 * Está ESTRICTAMENTE PROHIBIDO recuperar, almacenar en estado global o
 * renderizar el campo "password". Por esta razón, este modelo NO define
 * un campo password. Aunque la API devuelva esa propiedad en el JSON,
 * Gson simplemente la ignorará porque no existe un campo mapeado para
 * deserializarla, evitando que el dato llegue a memoria/estado de la app.
 */
public class User {

    @SerializedName("id")
    private final int id;

    @SerializedName("email")
    private final String email;

    @SerializedName("username")
    private final String username;

    @SerializedName("name")
    private final Name name;

    @SerializedName("address")
    private final Address address;

    @SerializedName("phone")
    private final String phone;

    // NOTA: NO existe "private String password;" a propósito.

    public User(int id, String email, String username, Name name, Address address, String phone) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public Name getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    /** name: { firstname, lastname } */
    public static class Name {
        @SerializedName("firstname")
        private final String firstname;
        @SerializedName("lastname")
        private final String lastname;

        public Name(String firstname, String lastname) {
            this.firstname = firstname;
            this.lastname = lastname;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public String getFullName() {
            return firstname + " " + lastname;
        }
    }

    /** address: { city, street, number, zipcode, geolocation } */
    public static class Address {
        @SerializedName("city")
        private final String city;
        @SerializedName("street")
        private final String street;
        @SerializedName("number")
        private final int number;
        @SerializedName("zipcode")
        private final String zipcode;
        @SerializedName("geolocation")
        private final Geolocation geolocation;

        public Address(String city, String street, int number, String zipcode, Geolocation geolocation) {
            this.city = city;
            this.street = street;
            this.number = number;
            this.zipcode = zipcode;
            this.geolocation = geolocation;
        }

        public String getCity() {
            return city;
        }

        public String getStreet() {
            return street;
        }

        public int getNumber() {
            return number;
        }

        public String getZipcode() {
            return zipcode;
        }

        public Geolocation getGeolocation() {
            return geolocation;
        }

        public String getFullAddress() {
            return street + " " + number + ", " + city + " (" + zipcode + ")";
        }
    }

    /** geolocation: { lat, long } */
    public static class Geolocation {
        @SerializedName("lat")
        private final String lat;
        @SerializedName("long")
        private final String lng;

        public Geolocation(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public String getLng() {
            return lng;
        }
    }
}
