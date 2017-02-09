import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.lx.service.OrderSecKillService;


@ContextConfiguration(value="classpath:spring/spring.xml")
public class OrderSecKillServiceImplTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private OrderSecKillService orderSecKillService;
	
	@Test
	public void secKillTest() {
		for(int i=0;i<50;i++){
			Thread thead = new Thread(new Runnable() {
				
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getId());
					try {
						System.out.println(orderSecKillService.secKill("111", "1001"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(new Integer(new Random().nextInt(10000)).longValue());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			thead.start();
		}
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
