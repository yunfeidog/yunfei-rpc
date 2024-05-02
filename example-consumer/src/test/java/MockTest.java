import com.yunfei.example.model.User;
import com.yunfei.example.service.UserService;
import com.yunfei.rpc.proxy.ServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyunfei
 */
@Slf4j
public class MockTest {
    public static void main(String[] args) {
        // 获取 代理
        UserService userService = ServiceProxyFactory.getMockProxy(UserService.class);
        short number = userService.getNumber();
        log.info("number:{}", number);
        User user = userService.getUser(new User());
        log.info("user:{}", user);
    }

}
