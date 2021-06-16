package GPS.proxies;

import GPS.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "userService", url = "localhost:9000")
public interface UserServiceProxy {
    @RequestMapping(method = RequestMethod.GET, value = "/getUser/{userName}")
    User getUser(@PathVariable("userName") String userName);

}
