import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.lx.service.OrderSecKillService;


@ContextConfiguration(value="classpath:spring/spring.xml")
public class SaveSecKillOrderServiceImplTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private OrderSecKillService orderSecKillService;
	
	@Test
	public void saveSecKillOrder() {
		for(int i=0;i<100;i++){
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {}
			orderSecKillService.saveSecKillOrder();
		}
	}

}
