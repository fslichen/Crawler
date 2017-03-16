package evolution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import evolution.service.ApartmentService458;

@SpringBootApplication
@RestController
public class Application {
	@Autowired
	private ApartmentService458 apartmentService458;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@GetMapping("/58")
	public void search58(String... arg0) {
		String url = "http://sz.58.com/chuzu/sub/l4000043/b11/?key=%E7%A7%9F%E6%88%BF&cmcskey=&final=1&specialtype=gls&pagetype=ditie";
		apartmentService458.search(url);
	}
}
