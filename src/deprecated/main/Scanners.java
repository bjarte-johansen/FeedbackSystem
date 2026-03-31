package root.main;


@FunctionalInterface
interface TriConsumer<A,B,C> {
    void accept(A a, B b, C c);
}

@FunctionalInterface
interface TriSupplier<A,B,C,D> {
    D get(A a, B b, C c);
}



@Deprecated
public class Scanners {

    public static void scanString(String s, TriSupplier<String, Integer, Integer, String> replace, TriConsumer<String, Integer, Integer> report) {
        System.out.println(s);
        __scanString(s, replace, report);
        System.out.println();
    }

    public static void __scanString(String s, TriSupplier<String, Integer, Integer, String> replace, TriConsumer<String, Integer, Integer> report) {
        int i, at, start = 0, n = s.length();

        while ((at = s.indexOf('@', start)) > -1) {
            i = at + 1;
            if (i < n && Character.isLetter(s.charAt(i))) {
                i++;
                while(i < n){
                    char ch = s.charAt(i);
                    if(!(Character.isLetterOrDigit(ch) || ch == '_')) break;
                    i++;
                }
            }

            if(at > start) report.accept(s, start, at);
            if(at + 1 < i) report.accept( replace.get(s, at + 1, i), 0, i - at + 1);

            start = i;
        }

        if(start < n) report.accept(s, start, n);
    }

    public static void __scanString2(String s, TriSupplier<String, Integer, Integer, String> replace, TriConsumer<String, Integer, Integer> report) {
        int start = 0, end = 0, offset = 0, n = s.length();

        while ((start = s.indexOf("{", offset)) > -1) {
            start++;
            if((end = s.indexOf("}", start)) == -1) break;

            if(start > offset) report.accept(s, offset, start - 1);

            String rep = replace.get(s, start, end);
            if(start < end) report.accept( rep, 0, rep.length());

            offset = end + 1;
        }

        if(offset < n) report.accept(s, offset, n);
    }

    public static void testScanString() {
        //String s = "Hello {user1}, how are you? {title1}{missingTitle}{title2}";
        String src = "1{a}2{b}3{c}4{d}5{e}6{f}{?}{g} {3210";
        __scanString2(
            src,
            (s, start, end) -> "<captured: " + s.substring(start, end) + ">",
            (s, start, end) -> System.out.println("\"" + s.substring(start, end) + "\"")
        );
    }
}
