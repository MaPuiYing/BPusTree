import java.util.Random;

public class Test {
	
	public static int getRandomNumberUsingInts(int min, int max) {
	    Random random = new Random();
	    return random.ints(min, max)
	      .findFirst()
	      .getAsInt();
	}
	
	
	
    public static void main(String[] args){

        BPlusTree<Product, Integer> b = new BPlusTree<>(4);
        
        
        Product p = new Product(4, null);
        b.insert(p, p.getId());
        
        Product a = new Product(getRandomNumberUsingInts(10,20), null);
        b.insert(a, a.getId());
        
        Product c = new Product(getRandomNumberUsingInts(10,20), null);
        b.insert(c, c.getId());
        
        Product d = new Product(getRandomNumberUsingInts(10,20), null);
        b.insert(d, d.getId());
        
        Product e = new Product(getRandomNumberUsingInts(10,20), null);
        b.insert(e, e.getId());

        

    }
}

