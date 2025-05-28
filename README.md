# Projeto Final - Engenharia de Software  
**Prof. Strauss**  
**Sistema de Gestão para Supermercado**  

**Alunos:**  
- Daniel Peçanha Ferreira  
- Filipe Souza Silva  
- Iago Ananias Silva  

---

## 1. Requisitos do Sistema  

### 1.1. Requisitos Funcionais  

#### **Cadastro de Produtos:**  
- Registrar novos produtos com:  
  - Nome, descrição, código de barras (EAN)  
  - Categoria, preço de custo, preço de venda  
  - Unidade de medida (Kg, L, un), fornecedor, estoque inicial  
- Editar e excluir produtos existentes.  

#### **Cadastro de Fornecedores:**  
- Registrar novos fornecedores com:  
  - Nome, CNPJ, telefone, e-mail, endereço  
- Editar e excluir fornecedores.  

#### **Gestão de Estoque:**  
- **Entrada de Mercadorias:** Registrar compras ou ajustes.  
- **Baixa de Estoque (Venda):** Subtrair automaticamente produtos vendidos.  
- **Ajuste de Estoque:** Correções manuais (perdas, devoluções).  
- **Consulta de Estoque:** Visualizar quantidades em tempo real.  
- **Alerta de Estoque Mínimo:** Notificar quando o estoque atingir limite.  

#### **Ponto de Venda (PDV):**  
- Interface intuitiva para registro de vendas.  
- Leitura de código de barras.  
- Cálculo automático do total.  
- Formas de pagamento: dinheiro, cartão.  
- Emissão de comprovante.  
- Cancelamento de item/venda antes da finalização.  

#### **Relatórios:**  
- Vendas por período.  
- Produtos mais/menos vendidos.  
- Estoque baixo.  
- Movimentação de estoque (entradas/saídas).  

### 1.2. Requisitos Não Funcionais  
- **Segurança:** Perfis de usuário (Gerente, Operador de Caixa).  
- **Usabilidade:** Interface intuitiva.  
- **Desempenho:** Respostas rápidas no PDV.  
- **Confiabilidade:** Integridade dos dados em falhas.  
- **Manutenção:** Código documentado e estruturado.  

---

## 2. Papéis e Responsabilidades  

### 2.1. Gerente  
**Privilégios Máximos:**  
- **Cadastros:**  
  - Produtos (CRUD completo).  
  - Fornecedores (CRUD completo).  
- **Estoque:**  
  - Registrar entradas/ajustes.  
  - Consultar todo o estoque.  
- **Relatórios:** Acesso total.  
- **Usuários:**  
  - Gerenciar Operadores de Caixa (cadastrar, redefinir senhas).  

### 2.2. Operador de Caixa  
**Funções Restritas:**  
- **PDV:**  
  - Iniciar/finalizar vendas.  
  - Processar pagamentos.  
  - Cancelar itens/vendas.  
  - Emitir comprovantes.  
- **Consultas:**  
  - Preços de produtos (sem acesso a custo/estoque).  
**Restrições:**  
- Não manipula cadastros ou estoque.  
- Sem acesso a relatórios gerenciais.  

---

## 3. Casos de Uso Críticos  

### **Caso de Uso 1: Registrar Nova Compra (Gerente)**  
**Fluxo Principal:**  
1. Acessar módulo de Estoque → "Registrar Entrada de Mercadorias".  
2. Informar fornecedor e nota fiscal.  
3. Selecionar produtos e quantidades recebidas.  
4. Sistema atualiza estoque e registra movimentação.  

### **Caso de Uso 2: Realizar Venda (Operador de Caixa)**  
**Fluxo Principal:**  
1. Acessar PDV → Iniciar nova venda.  
2. Escanear código de barras ou pesquisar produto.  
3. Sistema adiciona produto, calcula subtotal.  
4. Informar forma de pagamento → calcular troco (se necessário).  
5. Finalizar venda → baixa automática no estoque.  
6. Emitir comprovante e registrar venda no histórico.  

---  
**Documentação atualizada em:** `27/05/2025`  

