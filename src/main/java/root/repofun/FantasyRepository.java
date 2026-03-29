package root.repofun;

import root.ProxyRepository;
import java.util.List;

public interface FantasyRepository extends ProxyRepository<Fantasy, Long> {
    void sayHello();

    void delete(Fantasy fantasy);
    boolean existsById(long id);

    List<Fantasy> findByEmailLike(String emailPattern);

    Fantasy findByTextEquals(String text);
    Fantasy findByIdAndText(long id, String text);

    //Fantasy findByScoreAndEmail(int score, String email);

    int countByEmailLike(String emailPattern);
    int countByEmailLikeAndAge(String emailPattern, int age);

    boolean existsByEmailLike(String email);
}
