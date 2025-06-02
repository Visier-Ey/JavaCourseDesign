package org.JBR.Sqlite;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.JBR.DAO.BookDAO;

import java.util.Map;


public class AddFakeBooks {
    // 中文图书数据生成器
    private static final String[] GENRES = {
        "小说", "文学", "科幻", "悬疑", "历史", 
        "编程", "科学", "经济", "心理学", "传记"
    };
    
    private static final String[] AUTHORS = {
        "刘慈欣", "余华", "莫言", "金庸", "东野圭吾",
        "村上春树", "JK罗琳", "乔治·奥威尔", "钱钟书", "鲁迅",
        "马克·吐温", "海明威", "托尔斯泰", "陀思妥耶夫斯基"
    };
    
    private static final String[] PROGRAMMING_KEYWORDS = {
        "Java", "Python", "C++", "算法", "数据结构",
        "设计模式", "Spring", "数据库", "网络", "人工智能"
    };

    public static void run() {
        // 1. 创建BookDAO实例
        BookDAO bookDAO = new BookDAO("library.db");
        
        try {
            // 2. 生成50本随机图书数据
            List<BookData> fakeBooks = generateFakeBooks(50);
            
            // 3. 批量插入图书
            int successCount = 0;
            for (BookData book : fakeBooks) {
                boolean result = bookDAO.addBook(
                    book.bookId, 
                    book.title, 
                    book.author, 
                    book.isbn
                );
                if (result) successCount++;
            }
            
            System.out.printf("成功插入 %d/%d 本图书数据\n", successCount, fakeBooks.size());
            
            // 4. 验证插入结果
            List<Map<String, Object>> allBooks = bookDAO.getAllBooks();
            System.out.println("\n数据库中的前5本图书:");
            allBooks.stream().limit(5).forEach(System.out::println);
            
        } finally {
            // 5. 关闭数据库连接
            bookDAO.close();
        }
    }
    
    // 图书数据临时结构
    private static class BookData {
        String bookId;
        String title;
        String author;
        String isbn;
        
        BookData(String bookId, String title, String author, String isbn) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.isbn = isbn;
        }
    }
    
    // 生成伪造图书数据
    private static List<BookData> generateFakeBooks(int count) {
        List<BookData> books = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            String bookId = "BK" + String.format("%04d", i + 1);
            String title = generateTitle(random);
            String author = AUTHORS[random.nextInt(AUTHORS.length)];
            String isbn = generateISBN(random);
            
            books.add(new BookData(bookId, title, author, isbn));
        }
        
        return books;
    }
    
    // 生成中文书名
    private static String generateTitle(Random random) {
        String genre = GENRES[random.nextInt(GENRES.length)];
        
        if (genre.equals("编程")) {
            String keyword = PROGRAMMING_KEYWORDS[random.nextInt(PROGRAMMING_KEYWORDS.length)];
            return String.format("%s%s", keyword, 
                new String[]{"入门", "进阶", "实战", "精粹", "指南"}[random.nextInt(5)]);
        }
        
        return String.format("%s%s", genre, 
            new String[]{"的故事", "之谜", "简史", "回忆录", "全集"}[random.nextInt(5)]);
    }
    
    // 生成伪ISBN号
    private static String generateISBN(Random random) {
        return String.format("978-%d-%d-%d", 
            random.nextInt(900) + 100, 
            random.nextInt(90) + 10, 
            random.nextInt(9000) + 1000);
    }
}