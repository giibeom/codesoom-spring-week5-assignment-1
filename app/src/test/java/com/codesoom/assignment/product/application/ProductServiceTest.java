package com.codesoom.assignment.product.application;

import com.codesoom.assignment.product.exception.ProductNotFoundException;
import com.codesoom.assignment.product.adapter.out.persistence.FakeInMemoryProductRepository;
import com.codesoom.assignment.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.codesoom.assignment.support.IdFixture.ID_MAX;
import static com.codesoom.assignment.support.PagingFixture.PAGE_DEFAULT;
import static com.codesoom.assignment.support.PagingFixture.PAGE_SIZE_DEFAULT;
import static com.codesoom.assignment.support.ProductFixture.TOY_1;
import static com.codesoom.assignment.support.ProductFixture.TOY_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {
    private ProductService productService;

    private final PageRequest pageable = PageRequest.of(PAGE_DEFAULT.getValue(), PAGE_SIZE_DEFAULT.getValue());

    @BeforeEach
    void setUp() {
        FakeInMemoryProductRepository fakeProductRepository = new FakeInMemoryProductRepository();
        this.productService = new ProductService(fakeProductRepository);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class getProducts_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 등록된_장난감이_없을_때 {
            @Test
            @DisplayName("빈 리스트를 리턴한다")
            void it_returns_empty_list() {
                List<Product> products = productService.getProducts(pageable);

                assertThat(products).isEmpty();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 등록된_장난감이_있을_때 {

            @Nested
            @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
            class 상품_개수가_페이지_사이즈보다_적다면 {
                @DisplayName("n개의 장난감 목록을 리턴한다")
                @ParameterizedTest(name = "{0}개의 장난감 목록을 리턴한다")
                @ValueSource(ints = {1, 3, 7})
                void it_returns_list(int createCount) {
                    for (int i = 0; i < createCount; i++) {
                        productService.createProduct(TOY_1.생성_요청_데이터_생성());
                    }

                    List<Product> products = productService.getProducts(pageable);

                    assertThat(products)
                            .isNotEmpty()
                            .hasSize(createCount);
                }
            }

            @Nested
            @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
            class 상품_개수가_page_size보다_많다면 {
                @Test
                @DisplayName("pageSize만큼의 장난감 목록을 리턴한다")
                void it_returns_list() {
                    int createCount = pageable.getPageSize() * 2;

                    for (int i = 0; i < createCount; i++) {
                        productService.createProduct(TOY_1.생성_요청_데이터_생성());
                    }

                    List<Product> products = productService.getProducts(pageable);

                    assertThat(products)
                            .isNotEmpty()
                            .hasSize(pageable.getPageSize());
                }
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class getProduct_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 찾을_수_없는_id가_주어지면 {
            @Test
            @DisplayName("예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> productService.getProduct(ID_MAX.value()))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 찾을_수_있는_id가_주어지면 {
            @Test
            @DisplayName("해당 id의 장난감 정보를 리턴한다")
            void it_returns_product() {
                Product productSource = productService.createProduct(TOY_1.생성_요청_데이터_생성());

                Product product = productService.getProduct(productSource.getId());

                assertThat(product).isNotNull();
                assertThat(product.getId()).isEqualTo(productSource.getId());
                assertThat(product.getName()).isEqualTo(TOY_1.NAME());
                assertThat(product.getMaker()).isEqualTo(TOY_1.MAKER());
                assertThat(product.getPrice()).isEqualTo(TOY_1.PRICE());
                assertThat(product.getImageUrl()).isEqualTo(TOY_1.IMAGE());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class createProduct_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class null이_주어지면 {
            @Test
            @DisplayName("예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> productService.createProduct(null))
                        .isInstanceOf(NullPointerException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 새로운_상품이_주어지면 {
            @Test
            @DisplayName("상품을 저장하고 리턴한다")
            void it_returns_product() {
                Product product = productService.createProduct(TOY_1.생성_요청_데이터_생성());

                assertThat(product).isNotNull();
                assertThat(product.getName()).isEqualTo(TOY_1.NAME());
                assertThat(product.getMaker()).isEqualTo(TOY_1.MAKER());
                assertThat(product.getPrice()).isEqualTo(TOY_1.PRICE());
                assertThat(product.getImageUrl()).isEqualTo(TOY_1.IMAGE());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class updateProduct_메서드는 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 찾을_수_없는_id가_주어지면 {

            @Test
            @DisplayName("예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> productService.updateProduct(ID_MAX.value(), TOY_1.수정_요청_데이터_생성()))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 찾을_수_있는_id가_주어지면 {
            private Long fixtureId;

            @BeforeEach
            void setUpCreateFixture() {
                Product productSource = productService.createProduct(TOY_1.생성_요청_데이터_생성());
                fixtureId = productSource.getId();
            }

            @Test
            @DisplayName("상품을 수정하고 리턴한다")
            void it_returns_product() {
                Product product = productService.updateProduct(fixtureId, TOY_2.수정_요청_데이터_생성());

                assertThat(product).isNotNull();
                assertThat(product.getId()).isEqualTo(fixtureId);
                assertThat(product.getName()).isEqualTo(TOY_2.NAME());
                assertThat(product.getMaker()).isEqualTo(TOY_2.MAKER());
                assertThat(product.getPrice()).isEqualTo(TOY_2.PRICE());
                assertThat(product.getImageUrl()).isEqualTo(TOY_2.IMAGE());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class deleteProduct_메서드는 {
        private Long fixtureId;

        @BeforeEach
        void setUpCreateFixture() {
            Product productSource = productService.createProduct(TOY_1.생성_요청_데이터_생성());
            fixtureId = productSource.getId();
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 찾을_수_없는_id가_주어지면 {
            @Test
            @DisplayName("예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> productService.deleteProduct(ID_MAX.value()))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 찾을_수_있는_id가_주어지면 {
            @Test
            @DisplayName("상품을 삭제한다")
            void it_returns_product() {
                Product product = productService.getProduct(fixtureId);

                assertThat(product).isNotNull();

                productService.deleteProduct(fixtureId);

                assertThatThrownBy(() -> productService.getProduct(fixtureId))
                        .isInstanceOf(ProductNotFoundException.class);
            }
        }
    }
}
