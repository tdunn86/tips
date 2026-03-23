package com.model;

import java.util.ArrayList;

/**
 * Runs all presentation scenarios in sequence without user input.
 * Output is identical to what TIPSUI would produce -- the same menus,
 * prompts, and display methods are used. Hardcoded inputs are printed
 * inline so the output reads like a real interactive session.
 *
 * At the end of the demo, cleanup() removes Sally's account, the Longest
 * Subarray question, and Jimmy's comment so the driver can be re-run
 * cleanly from the same starting state.
 * @author TIPS Team
 */
public class ScenarioDriver {

    private static final TIPSFacade facade = TIPSFacade.getInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("============================================");
        System.out.println("       Welcome to the TIPS System");
        System.out.println("============================================");

        scenario0_beforeState();
        scenario1_duplicateUser();
        scenario2_sallyCreatesAccount();
        scenario3_sallyCreatesQuestion();
        scenario4_jimmyDailyTask();
        scenario5_afterState();
        cleanup();
    }

    // ================================================================
    // SCENARIO 1 - Create Account: Duplicate User
    // ================================================================
    private static void scenario1_duplicateUser() throws Exception {
        pause("=== SCENARIO 1: Create Account - Duplicate User ===");

        System.out.println("\n1. Login\n2. Create Account\n3. Exit");
        System.out.print("Choose an option: ");
        System.out.println("2");

        System.out.println("\n--- Create Account ---");
        System.out.print("Username: "); System.out.println("SSparrow");
        System.out.print("Password: "); System.out.println("sally26");
        System.out.print("Email: ");    System.out.println("sally@email.com");

        facade.registerUser("SSparrow", "sally26", "sally@email.com", AccountType.EDITOR);

        pause("=== END SCENARIO 1 ===");
    }

    // ================================================================
    // SCENARIO 2 - Create Account: Success + Login
    // ================================================================
    private static void scenario2_sallyCreatesAccount() throws Exception {
        pause("=== SCENARIO 2: Create Account - Success ===");

        System.out.println("\n1. Login\n2. Create Account\n3. Exit");
        System.out.print("Choose an option: ");
        System.out.println("2");

        System.out.println("\n--- Create Account ---");
        System.out.print("Username: "); System.out.println("SallySparrow");
        System.out.print("Password: "); System.out.println("sally26");
        System.out.print("Email: ");    System.out.println("sally@email.com");

        User sally = facade.registerUser("SallySparrow", "sally26",
                "sally@email.com", AccountType.EDITOR);
        if (sally != null)
            System.out.println("Account created! Welcome, " + sally.getUsername());

        facade.login("SallySparrow", "sally26");
        User u = facade.getCurrentUser();
        System.out.println("Welcome, " + u.getUsername() + " (" + u.getAccountType() + ")");

        System.out.println("\n--- Main Menu ---");
        System.out.println("1. View all questions");
        System.out.println("2. Search questions");
        System.out.println("3. Add a question");
        System.out.println("4. Logout");

        pause("=== END SCENARIO 2 ===");
    }

    // ================================================================
    // SCENARIO 3 - Sally Creates a Question and Two Solutions
    // ================================================================
    private static void scenario3_sallyCreatesQuestion() throws Exception {
        pause("=== SCENARIO 3: Sally Creates a Question and Two Solutions ===");

        System.out.println("\n--- Main Menu ---");
        System.out.println("1. View all questions");
        System.out.println("2. Search questions");
        System.out.println("3. Add a question");
        System.out.println("4. Logout");
        System.out.print("Choose an option: ");
        System.out.println("3");

        System.out.println("\n--- Add Question ---");

        String title  = "Longest Subarray with Sum K";
        String prompt =
            "Given an integer array nums and an integer k, return the length of the\n" +
            "longest contiguous subarray whose total equals k.\n" +
            "Note: the array can contain negative numbers.\n\n" +
            "Example 1:\n" +
            "  Input:  nums = [1, -1, 5, -2, 3], k = 3\n" +
            "  Output: 4\n" +
            "  Explanation: The subarray [1, -1, 5, -2] sums to 3 and has length 4.\n\n" +
            "Example 2:\n" +
            "  Input:  nums = [-2, -1, 2, 1], k = 3\n" +
            "  Output: 2\n\n" +
            "Follow-up: What is the time complexity? Can you make it faster?\n\n" +
            "Write a method with this signature:\n" +
            "  public int longestSubarray(int[] nums, int k)";

        System.out.print("Title: ");   System.out.println(title);
        System.out.print("Prompt: ");  System.out.println("(multi-line prompt entered)");
        System.out.print("Difficulty (EASY, MEDIUM, HARD): ");                    System.out.println("MEDIUM");
        System.out.print("Language (JAVA, PYTHON, CPP, JAVASCRIPT, HTML, CSS): "); System.out.println("JAVA");
        System.out.print("Course (CSCE145, CSCE146, CSCE240, CSCE242, CSCE247): "); System.out.println("CSCE247");

        Question q = facade.addQuestion(title, prompt, Difficulty.MEDIUM, Language.JAVA, Course.CSCE247);

        if (q != null) {
            q.setHint("Try using a HashMap to store the first occurrence of each prefix sum. " +
                      "If (prefixSum - k) exists in the map, you found a valid subarray.");
            q.setSampleSolution(
                "// Solution 1 - Brute Force O(n^2)\n" +
                "public int longestSubarrayBruteForce(int[] nums, int k) {\n" +
                "    int maxLength = 0;\n" +
                "    for (int i = 0; i < nums.length; i++) {\n" +
                "        int sum = 0;\n" +
                "        for (int j = i; j < nums.length; j++) {\n" +
                "            sum += nums[j];\n" +
                "            if (sum == k)\n" +
                "                maxLength = Math.max(maxLength, j - i + 1);\n" +
                "        }\n" +
                "    }\n" +
                "    return maxLength;\n" +
                "}\n\n" +
                "// Solution 2 - HashMap O(n)\n" +
                "public int longestSubarray(int[] nums, int k) {\n" +
                "    Map<Integer, Integer> map = new HashMap<>();\n" +
                "    int prefixSum = 0, maxLength = 0;\n" +
                "    for (int i = 0; i < nums.length; i++) {\n" +
                "        prefixSum += nums[i];\n" +
                "        if (prefixSum == k) maxLength = i + 1;\n" +
                "        if (map.containsKey(prefixSum - k))\n" +
                "            maxLength = Math.max(maxLength, i - map.get(prefixSum - k));\n" +
                "        if (!map.containsKey(prefixSum)) map.put(prefixSum, i);\n" +
                "    }\n" +
                "    return maxLength;\n" +
                "}"
            );
            q.setSampleExplanation(
                "Solution 1 (Brute Force): Try every subarray pair (i,j) and compute the sum. O(n^2) time.\n" +
                "Solution 2 (HashMap): Track prefix sums. If (prefixSum - k) is in the map, the subarray\n" +
                "between that index and current sums to k. Store only the FIRST occurrence of each prefix\n" +
                "sum to maximize length. O(n) time, O(n) space."
            );

            System.out.println("Question added!");
            printQuestionHeader(q);

            // ---- Solution 1: Brute Force (real code) ----
            System.out.println("\n1. Write and submit code");
            System.out.println("2. View sample solution");
            System.out.println("3. Add a comment");
            System.out.println("4. Reply to a comment");
            System.out.println("5. Remove a comment");
            System.out.println("6. Go back");
            System.out.print("Choose an option: ");
            System.out.println("1");

            String bruteForce =
                "public int longestSubarrayBruteForce(int[] nums, int k) {\n" +
                "    int maxLength = 0;\n" +
                "    for (int i = 0; i < nums.length; i++) {\n" +
                "        int sum = 0;\n" +
                "        for (int j = i; j < nums.length; j++) {\n" +
                "            sum += nums[j];\n" +
                "            if (sum == k)\n" +
                "                maxLength = Math.max(maxLength, j - i + 1);\n" +
                "        }\n" +
                "    }\n" +
                "    return maxLength;\n" +
                "}";

            System.out.println("\n--- Code Editor [JAVA] ---");
            System.out.println(":submit = submit  |  :clear = clear  |  :quit = cancel");
            System.out.println("--------------------------------------------");
            printNumberedCode(bruteForce);
            System.out.println("     | :submit");

            facade.submitSolution(q, bruteForce);
            System.out.println("\nSubmitted! Your code:");
            printNumberedCode(bruteForce);
            System.out.print("\nCompare with sample solution? (yes/no): ");
            System.out.println("no");

            // ---- Solution 2: HashMap (real code) ----
            System.out.println("\n1. Write and submit code");
            System.out.println("2. View sample solution");
            System.out.println("3. Add a comment");
            System.out.println("4. Reply to a comment");
            System.out.println("5. Remove a comment");
            System.out.println("6. Go back");
            System.out.print("Choose an option: ");
            System.out.println("1");

            String hashMap =
                "public int longestSubarray(int[] nums, int k) {\n" +
                "    Map<Integer, Integer> map = new HashMap<>();\n" +
                "    int prefixSum = 0, maxLength = 0;\n" +
                "    for (int i = 0; i < nums.length; i++) {\n" +
                "        prefixSum += nums[i];\n" +
                "        if (prefixSum == k) maxLength = i + 1;\n" +
                "        if (map.containsKey(prefixSum - k))\n" +
                "            maxLength = Math.max(maxLength, i - map.get(prefixSum - k));\n" +
                "        if (!map.containsKey(prefixSum)) map.put(prefixSum, i);\n" +
                "    }\n" +
                "    return maxLength;\n" +
                "}";

            System.out.println("\n--- Code Editor [JAVA] ---");
            System.out.println(":submit = submit  |  :clear = clear  |  :quit = cancel");
            System.out.println("--------------------------------------------");
            printNumberedCode(hashMap);
            System.out.println("     | :submit");

            facade.submitSolution(q, hashMap);
            System.out.println("\nSubmitted! Your code:");
            printNumberedCode(hashMap);
            System.out.print("\nCompare with sample solution? (yes/no): ");
            System.out.println("no");

            // Sally logs out
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View all questions");
            System.out.println("2. Search questions");
            System.out.println("3. Add a question");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            System.out.println("4");

            facade.saveAll();
            facade.logout();
            System.out.println("Logged out.");
        }

        pause("=== END SCENARIO 3 ===");
    }

    // ================================================================
    // SCENARIO 4 - Jimmy Bauer Completes Daily Task
    // ================================================================
    private static void scenario4_jimmyDailyTask() throws Exception {
        pause("=== SCENARIO 4: Jimmy Bauer Completes Daily Task ===");

        System.out.println("\n1. Login\n2. Create Account\n3. Exit");
        System.out.print("Choose an option: ");
        System.out.println("1");

        System.out.println("\n--- Login ---");
        System.out.print("Username: "); System.out.println("JimmyBauer");
        System.out.print("Password: "); System.out.println("bauer26");

        facade.login("JimmyBauer", "bauer26");
        User jimmy = facade.getCurrentUser();
        Student jimmyStudent = (Student) jimmy;
        System.out.println("Welcome, " + jimmy.getUsername() + " (" + jimmy.getAccountType() + ")");

        // Main menu — daily challenge is a menu option, not automatic
        System.out.println("\n--- Main Menu ---");
        System.out.println("Current streak: " + jimmyStudent.getStreak() + " days");
        System.out.println("1. View all questions");
        System.out.println("2. Search questions");
        System.out.println("3. Daily challenge");
        System.out.println("4. Logout");
        System.out.print("Choose an option: ");
        System.out.println("3");

        // Jimmy selects daily challenge
        System.out.println("\n--- Daily Challenge ---");
        System.out.println("Based on your skill level (" + jimmyStudent.getClassification()
                + "), here is today's challenge:");

        Question daily = facade.getDailyChallenge();

        if (daily != null) {
            printQuestionHeader(daily);

            // Jimmy reviews the solutions
            System.out.println("\n1. Write and submit code");
            System.out.println("2. View sample solution");
            System.out.println("3. Add a comment");
            System.out.println("4. Reply to a comment");
            System.out.println("5. Remove a comment");
            System.out.println("6. Go back");
            System.out.print("Choose an option: ");
            System.out.println("2");

            handleViewSampleSolution(daily);

            // Jimmy is confused — adds a comment
            System.out.println("\n1. Write and submit code");
            System.out.println("2. View sample solution");
            System.out.println("3. Add a comment");
            System.out.println("4. Reply to a comment");
            System.out.println("5. Remove a comment");
            System.out.println("6. Go back");
            System.out.print("Choose an option: ");
            System.out.println("3");

            System.out.println();
            System.out.print("Title: ");
            System.out.println("Confused about Solution 2");
            System.out.print("Comment: ");
            System.out.println("I don't understand why resetting one pointer to the start " +
                    "finds the duplicate. Can someone explain the math behind why the " +
                    "two pointers meet at the cycle entrance?");

            Reply comment = facade.addComment(
                daily,
                "Confused about Solution 2",
                "I don't understand why resetting one pointer to the start finds the duplicate. " +
                "Can someone explain the math behind why the two pointers meet at the cycle entrance?"
            );
            System.out.println("Comment added!");

            System.out.println("\n--------------------------------------------");
            System.out.println("Comments:");
            printReply(comment, 1);

            // Jimmy goes back to main menu and prints to file
            System.out.println("\n1. Write and submit code");
            System.out.println("2. View sample solution");
            System.out.println("3. Add a comment");
            System.out.println("4. Reply to a comment");
            System.out.println("5. Remove a comment");
            System.out.println("6. Go back");
            System.out.print("Choose an option: ");
            System.out.println("6");

            System.out.println("\n--- Main Menu ---");
            System.out.println("Current streak: " + jimmyStudent.getStreak() + " days");
            System.out.println("1. View all questions");
            System.out.println("2. Search questions");
            System.out.println("3. Daily challenge");
            System.out.println("4. Logout");

            System.out.println("\n>> Printing question to file for offline review...");
            facade.printQuestionToFile(daily, "jimmy_offline_review.txt");

            // Jimmy searches for Binary Search Tree
            System.out.print("Choose an option: ");
            System.out.println("2");
            System.out.print("\nSearch keyword: ");
            System.out.println("Binary Search Tree");

            ArrayList<Question> bstResults = facade.getQuestions("Binary Search Tree");

            System.out.println("\n--- Questions ---");
            for (int i = 0; i < bstResults.size(); i++) {
                Question bq = bstResults.get(i);
                System.out.printf("%d. [%s] %s (%s)%n",
                        i + 1, bq.getDifficulty(), bq.getTitle(), bq.getCourse());
            }

            System.out.print("\nEnter number to view (0 to go back): ");
            System.out.println("1");
            printQuestionHeader(bstResults.get(0));

            System.out.println("\n1. Write and submit code");
            System.out.println("2. View sample solution");
            System.out.println("3. Add a comment");
            System.out.println("4. Reply to a comment");
            System.out.println("5. Remove a comment");
            System.out.println("6. Go back");
            System.out.print("Choose an option: ");
            System.out.println("6");

            System.out.print("\nEnter number to view (0 to go back): ");
            System.out.println("2");
            printQuestionHeader(bstResults.get(1));

            System.out.println("\n1. Write and submit code");
            System.out.println("2. View sample solution");
            System.out.println("3. Add a comment");
            System.out.println("4. Reply to a comment");
            System.out.println("5. Remove a comment");
            System.out.println("6. Go back");
            System.out.print("Choose an option: ");
            System.out.println("6");

            // Show updated streak after completing the daily challenge
            System.out.println("\n--- Main Menu ---");
            System.out.println("Current streak: " + jimmyStudent.getStreak() + " days");
            System.out.println("1. View all questions");
            System.out.println("2. Search questions");
            System.out.println("3. Daily challenge");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            System.out.println("4");

            facade.logout();
            System.out.println("Logged out.");
        }

        pause("=== END SCENARIO 4 ===");
    }

    // ================================================================
    // SCENARIO 0 - Data Persistence: BEFORE STATE
    // ================================================================
    private static void scenario0_beforeState() throws Exception {
        pause("=== DATA PERSISTENCE: BEFORE THE DEMO ===");

        System.out.println("Showing the state of the JSON files BEFORE the demo runs.");
        System.out.println("Sally Sparrow does not exist yet. Sullivan and Jimmy are already in the system.");
        System.out.println("The Longest Subarray question has not been added yet.");

        System.out.println("\n--- users.json ---");
        ArrayList<User> users = DataLoader.getUsers();
        for (User u : users) {
            String extra = "";
            if (u instanceof Student) {
                Student s = (Student) u;
                extra = "  streak=" + s.getStreak() + "  class=" + s.getClassification();
            }
            System.out.printf("  [%-8s]  %-20s%s%n", u.getAccountType(), u.getUsername(), extra);
        }

        System.out.println("\n--- questions.json ---");
        ArrayList<Question> questions = DataLoader.getQuestions();
        for (Question q : questions) {
            System.out.printf("  [%-6s]  %-45s  %d solution(s)  %d comment(s)%n",
                    q.getDifficulty(), q.getTitle(),
                    q.getSolutions().size(), q.getReplies().size());
        }

        System.out.println("\n--- Verification ---");
        boolean sallyPresent    = users.stream().anyMatch(u -> u.getUsername().equals("SallySparrow"));
        boolean sullivanPresent = users.stream().anyMatch(u -> u.getUsername().equals("SSparrow"));
        boolean jimmyPresent    = users.stream().anyMatch(u -> u.getUsername().equals("JimmyBauer"));
        boolean longestPresent  = questions.stream().anyMatch(q -> q.getTitle().contains("Longest Subarray"));

        int jimmyStreak = users.stream()
                .filter(u -> u.getUsername().equals("JimmyBauer"))
                .map(u -> ((Student) u).getStreak())
                .findFirst().orElse(0);

        System.out.println("  Sally Sparrow in file:         " + sallyPresent   + "  (not added yet)");
        System.out.println("  SSparrow (Sullivan) in file:   " + sullivanPresent + "  (already exists)");
        System.out.println("  Jimmy Bauer in file:           " + jimmyPresent   + "  (already exists)");
        System.out.println("  Jimmy's current streak:        " + jimmyStreak    + " days");
        System.out.println("  Longest Subarray in file:      " + longestPresent  + "  (not added yet)");

        pause("=== END BEFORE STATE ===");
    }

    // ================================================================
    // SCENARIO 5 - Data Persistence: AFTER STATE
    // ================================================================
    private static void scenario5_afterState() throws Exception {
        pause("=== DATA PERSISTENCE: AFTER THE DEMO ===");

        System.out.println("Showing the state of the JSON files AFTER the demo runs.");

        System.out.println("\n--- users.json ---");
        ArrayList<User> users = DataLoader.getUsers();
        for (User u : users) {
            String extra = "";
            if (u instanceof Student) {
                Student s = (Student) u;
                extra = "  streak=" + s.getStreak() + "  class=" + s.getClassification();
            }
            System.out.printf("  [%-8s]  %-20s%s%n", u.getAccountType(), u.getUsername(), extra);
        }

        System.out.println("\n--- questions.json ---");
        ArrayList<Question> questions = DataLoader.getQuestions();
        for (Question q : questions) {
            System.out.printf("  [%-6s]  %-45s  %d solution(s)  %d comment(s)%n",
                    q.getDifficulty(), q.getTitle(),
                    q.getSolutions().size(), q.getReplies().size());
        }

        System.out.println("\n--- Verification ---");
        boolean sallyFound   = users.stream().anyMatch(u -> u.getUsername().equals("SallySparrow"));
        boolean jimmyFound   = users.stream().anyMatch(u -> u.getUsername().equals("JimmyBauer"));
        boolean longestFound = questions.stream().anyMatch(q -> q.getTitle().contains("Longest Subarray"));
        boolean bst1Found    = questions.stream().anyMatch(q -> q.getTitle().contains("Binary Search Tree - Insert"));
        boolean bst2Found    = questions.stream().anyMatch(q -> q.getTitle().contains("Binary Search Tree - Validate"));
        boolean commentSaved = questions.stream().anyMatch(q ->
                q.getReplies().stream().anyMatch(r ->
                        r.getAuthor().getUsername().equals("JimmyBauer")));
        int jimmyStreak = users.stream()
                .filter(u -> u.getUsername().equals("JimmyBauer"))
                .map(u -> ((Student) u).getStreak())
                .findFirst().orElse(0);

        System.out.println("  Sally Sparrow added:           " + sallyFound);
        System.out.println("  Jimmy Bauer present:           " + jimmyFound);
        System.out.println("  Jimmy's streak saved:          " + jimmyStreak + " days");
        System.out.println("  Jimmy's comment saved:         " + commentSaved);
        System.out.println("  Longest Subarray question:     " + longestFound);
        System.out.println("  BST Insert question:           " + bst1Found);
        System.out.println("  BST Validate question:         " + bst2Found);

        System.out.println("\n============================================");
        System.out.println("           End of Demo");
        System.out.println("============================================");
    }

    // ================================================================
    // CLEANUP - runs silently after demo so driver can be re-run cleanly
    // ================================================================
    private static void cleanup() {
        User sally = UserList.getInstance().getUser("SallySparrow");
        if (sally != null) UserList.getInstance().removeUser(sally);

        QuestionList questionList = QuestionList.getInstance();
        questionList.getAllQuestions().stream()
                .filter(q -> q.getTitle().equals("Longest Subarray with Sum K"))
                .findFirst()
                .ifPresent(questionList::removeQuestion);

        questionList.getAllQuestions().forEach(q ->
            q.getReplies().removeIf(r -> r.getAuthor().getUsername().equals("JimmyBauer"))
        );

        User jimmy = UserList.getInstance().getUser("JimmyBauer");
        if (jimmy instanceof Student) ((Student) jimmy).setStreak(8);

        DataWriter.saveUsers();
        DataWriter.saveQuestions();
    }

    // ================================================================
    // Display helpers -- identical to TIPSUI
    // ================================================================

    private static void printQuestionHeader(Question question) {
        System.out.println("\n============================================");
        System.out.println("Title:      " + question.getTitle());
        System.out.println("Course:     " + question.getCourse());
        System.out.println("Difficulty: " + question.getDifficulty());
        System.out.println("Language:   " + question.getLanguage());
        if (question.getAuthor() != null)
            System.out.println("Author:     " + question.getAuthor().getUsername());
        System.out.println("--------------------------------------------");
        System.out.println(question.getPrompt());
        if (question.getHint() != null && !question.getHint().isEmpty())
            System.out.println("\nHint: " + question.getHint());
        if (!question.getSolutions().isEmpty()) {
            System.out.println("--------------------------------------------");
            System.out.println("Submissions: " + question.getSolutions().size());
        }
        if (question.isSolutionRevealed()) {
            System.out.println("--------------------------------------------");
            System.out.println("SAMPLE SOLUTION:");
            printNumberedCode(question.getSampleSolution());
            System.out.println("Explanation: " + question.getSampleExplanation());
        }
        if (!question.getReplies().isEmpty()) {
            System.out.println("--------------------------------------------");
            System.out.println("Comments:");
            question.getReplies().forEach(r -> printReply(r, 1));
        }
        System.out.println("============================================");
    }

    private static void handleViewSampleSolution(Question question) {
        if (question.getSampleSolution() == null) {
            System.out.println("No sample solution available.");
            return;
        }
        facade.revealSolution(question);
        System.out.println("\n--- SAMPLE SOLUTION ---");
        printNumberedCode(question.getSampleSolution());
        System.out.println("\nExplanation: " + question.getSampleExplanation());
    }

    private static void printReply(Reply reply, int depth) {
        String indent = "  ".repeat(depth);
        System.out.printf("%s[%s] %s  |  %s  (%d upvotes)%n",
                indent,
                reply.getAuthor().getUsername(),
                reply.getTitle(),
                reply.getDatePosted(),
                reply.getUpvotes());
        System.out.println(indent + reply.getContent());
        reply.getReplies().forEach(nested -> printReply(nested, depth + 1));
    }

    private static void printNumberedCode(String code) {
        if (code == null || code.isEmpty()) { System.out.println("  (empty)"); return; }
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++)
            System.out.printf("%3d | %s%n", i + 1, lines[i]);
    }

    private static void pause(String label) throws Exception {
        System.out.println("\n--------------------------------------------");
        System.out.println(label);
        System.out.println("--------------------------------------------");
        System.out.println("[ Press Enter to continue... ]");
        System.in.read();
        while (System.in.available() > 0) System.in.read();
        System.out.println();
    }
}
