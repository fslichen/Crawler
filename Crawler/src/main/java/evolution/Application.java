package evolution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import evolution.service.ApartmentService;

@SpringBootApplication
public class Application implements CommandLineRunner {
	@Autowired
	private ApartmentService apartmentService;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public void run(String... arg0) throws Exception {
		String url = "http://sz.58.com/chuzu/sub/l4000043/b11/?key=%E7%A7%9F%E6%88%BF&cmcskey=&final=1&specialtype=gls&pagetype=ditie";
		apartmentService.search(url);
	}
}
