public class Product {
    private Integer id;
    private String name;
    private Double price;

    public Product(Integer id, Double data) {
        this.id = id;
        this.price = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}


