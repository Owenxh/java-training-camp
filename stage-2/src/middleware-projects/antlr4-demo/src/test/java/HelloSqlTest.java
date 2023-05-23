import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class HelloSqlTest {

    private HelloSqlParser.QuerySpecificationContext parseSelectSql(String sql) {
        HelloSqlLexer baseSqlLexer = new HelloSqlLexer(CharStreams.fromString(sql));
        CommonTokenStream tokens = new CommonTokenStream(baseSqlLexer);
        HelloSqlParser parser = new HelloSqlParser(tokens);
        return parser.querySpecification();
    }

    @ValueSource(strings = {
            """
            select * from users;
            """,
            """
            select * from users_from;
            """
    })
    @ParameterizedTest
    public void testSelectStatementWithoutFromClause(String sql) {
        HelloSqlParser.QuerySpecificationContext ctx = parseSelectSql(sql);
        assertEquals("select", ctx.SELECT().getSymbol().getText());
        assertNull(ctx.fromClause().whereExpr);
    }

    @ValueSource(strings = {
            """
            select * from users u where u.name = 'owen' and u.age = 18;
            """
    })
    @ParameterizedTest
    public void testSelectStatementWithFromClause(String sql) {
        HelloSqlParser.QuerySpecificationContext ctx = parseSelectSql(sql);
        assertEquals("select", ctx.SELECT().getSymbol().getText());
        List<ParseTree> children = ctx.fromClause().whereExpr.children;
        assertEquals(3, children.size());
        assertEquals("u.name='owen'", children.get(0).getText());
        assertEquals("and", children.get(1).getText());
        assertEquals("u.age=18", children.get(2).getText());
    }

    @ValueSource(strings = {
            """
            select name from users where age = 18 order by name desc;
            """
    })
    @ParameterizedTest
    public void testSelectStatementWithOrderBy(String sql) {
        HelloSqlParser.QuerySpecificationContext ctx = parseSelectSql(sql);
        HelloSqlParser.OrderByClauseContext orderByCtx = ctx.orderByClause();
        List<ParseTree> children = orderByCtx.children;
        assertEquals("order", children.get(0).getText());
        assertEquals("desc", orderByCtx.orderByExpression().get(0).order.getText());
    }

    @ValueSource(strings = {
            """
            select name, age from users;
            """
    })
    @ParameterizedTest
    public void testSelectElements(String sql) {
        HelloSqlParser.QuerySpecificationContext ctx = parseSelectSql(sql);
        List<HelloSqlParser.SelectElementContext> selectElementContexts = ctx.selectElements().selectElement();
        assertEquals(2, selectElementContexts.size());
        assertEquals("name", selectElementContexts.get(0).getText());
        assertEquals("age", selectElementContexts.get(1).getText());
    }
}
