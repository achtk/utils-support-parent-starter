package com.chua.common.support.objects.source;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.scanner.BaseAnnotationResourceScanner;
import com.chua.common.support.spi.ServiceProvider;

import java.util.List;
import java.util.Set;

/**
 * 类型定义源
 *
 * @author CH
 * @since 2023/09/02
 */
@SuppressWarnings("ALL")
@Spi("type")
public class ClassTypeDefinitionSource extends AbstractTypeDefinitionSource implements InitializingAware {


    private List<BaseAnnotationResourceScanner> scanner;

    public ClassTypeDefinitionSource(ConfigureContextConfiguration configuration) {
        super(configuration);
        afterPropertiesSet();
    }

    @Override
    public boolean isMatch(TypeDefinition typeDefinition) {
        return false;
    }

    @Override
    public void afterPropertiesSet() {
        this.scanner = ServiceProvider.of(BaseAnnotationResourceScanner.class).collect(new Object[]{configuration.packages()});
        for (BaseAnnotationResourceScanner baseAnnotationResourceScanner : scanner) {
            Set scan = baseAnnotationResourceScanner.scan();
            for (Object o : scan) {
                register(o);
            }
        }
    }


}
