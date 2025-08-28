///usr/bin/env jbang "$0" "$@" ; exit $?
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NetStatParser
{
    public static void main(String[] args)
    {
        List<String> lines;
        if (args.length == 0)
        {
            lines = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            String line;
            try
            {
                while ((line = reader.readLine()) != null)
                {
                    lines.add(line);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        } else
        {
            try
            {
                lines = Files.lines(Paths.get(args[0])).toList();
            } catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }

        String protocol = "";
        String localAddress = "";
        String foreignAddress = "";
        String state = "";
        String serviceName = "";

        for (String l : lines)
        {
            if (l.isBlank()) continue;

            if ("Active Connections".equals(l)) continue;

            l = l.trim();

            String[] split = l.split("\\s+");

            String first = split[0];

            if ("UDP".equals(first) || "TCP".equals(first))
            {
                serviceName = "";

                protocol = split[0];
                localAddress = split[1];
                foreignAddress = split[2];

                if (split.length == 4)
                {
                    state = split[3];
                } else
                {
                    state = "";
                }

                continue;
            }

            // If we get here, we are either at the service name (optional) or the exe name.

            // If starts with '[' then it is the process name
            if (first.charAt(0) == '[')
            {
                String app = first.substring(1, first.length() - 1);

                System.out.printf("%s, %s, %s, %s, %s, %s%n", app, protocol, localAddress, foreignAddress, serviceName, state);
            }
        }
    }
}
