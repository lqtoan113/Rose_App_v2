package com.rose.config;

import io.rocketbase.mail.EmailTemplateBuilder;
import io.rocketbase.mail.TableLine;
import io.rocketbase.mail.model.HtmlTextEmail;
import io.rocketbase.mail.styling.Alignment;
import io.rocketbase.mail.table.ColumnConfig;
import io.rocketbase.mail.table.TableCellHtml;
import io.rocketbase.mail.table.TableCellImage;
import io.rocketbase.mail.table.TableCellLink;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class CustomTableConfig implements TableLine {

    List<List<Object>> headerRows = new ArrayList<>();
    List<List<Object>> itemRows = new ArrayList<>();
    List<List<Object>> footerRows = new ArrayList<>();
    @Getter(AccessLevel.PRIVATE)
    EmailTemplateBuilder.EmailTemplateConfigBuilder builder;
    private AtomicInteger posCounter = new AtomicInteger(1);

    public CustomTableConfig(EmailTemplateBuilder.EmailTemplateConfigBuilder builder) {
        this.builder = builder;

        headerRows.add(Arrays.asList("Pos", "Description", "Quantity", "Amount"));
    }

    @Override
    public EmailTemplateBuilder.EmailTemplateConfigBuilder and() {
        return builder;
    }

    @Override
    public HtmlTextEmail build() {
        return builder.build();
    }

    public CustomTableConfig itemRow(TableCellImage image, TableCellLink description, String quantity, Double amount) {
        itemRows.add(Arrays.asList(posCounter.getAndIncrement(), image, description, quantity, amount));
        return this;
    }

    public CustomTableConfig footerRow(TableCellHtml label, TableCellHtml amount) {
        footerRows.add(Arrays.asList(label, amount));
        return this;
    }

    @Override
    public List<ColumnConfig> getHeader() {
        return Arrays.asList(new ColumnConfig()
                        .center(),
                new ColumnConfig()
                        .colspan(2)
                        .width("60%"),
                new ColumnConfig()
                        .alignment(Alignment.RIGHT),
                new ColumnConfig()
                        .width("20%")
                        .alignment(Alignment.RIGHT));
    }

    @Override
    public List<ColumnConfig> getItem() {
        return Arrays.asList(new ColumnConfig().center(),
                new ColumnConfig()
                        .width(90),
                new ColumnConfig()
                        .lighter(),
                new ColumnConfig()
                        .italic()
                        .right(),
                new ColumnConfig()
                        .numberFormat("#.## '$'")
                        .right());
    }

    @Override
    public List<ColumnConfig> getFooter() {
        return Arrays.asList(new ColumnConfig()
                        .colspan(4)
                        .alignment(Alignment.RIGHT),
                new ColumnConfig()
                        .alignment(Alignment.RIGHT));
    }
}
