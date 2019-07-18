import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("cn.pengpeng.mapper")
@ComponentScan(basePackages ={"org.n3r.idworker","cn.pengpeng"})
public class PengpengApplication {

	public static void main(String[] args) {
		SpringApplication.run(PengpengApplication.class, args);
	}


}
