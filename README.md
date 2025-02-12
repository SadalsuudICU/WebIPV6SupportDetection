# Website IPv6 Support Checker

This tool is used to test the IPv6 support for a website. It performs the following checks: 

- Verifies IPv6 accessibility for the homepage
- Checks the IPv6 support for second-level and third-level internal links
- Checks for the presence of AAAA records
- Compares the content similarity between IPv4 and IPv6 pages



## How to Use 

1. Clone or download this project

2. Configure `ipv6Url` and `ipv4Url` in the code

3. Run the program to generate the IPv6 support report 

   

## Example Code 

```java
IPv6SupportChecker checker = new IPv6SupportChecker("https://www.example.com", "https://www.example.com");
checker.generateReport();
```

