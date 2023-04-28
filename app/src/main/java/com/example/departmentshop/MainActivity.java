package com.example.departmentshop;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etNome, etCpf, etItem, etQuantidade, etValorUnitario, etParcelas;
    private Button btnAdicionar, btnConcluir;
    private TextView tvLista, tvTotal, tvTotalItens, tvParcelas, tvValorTotal;
    private Spinner spPagamento;

    private List<String> pedidos = new ArrayList<>();
    private int pedidoId = 1;
    private int totalItens = 0;
    private double totalValor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        etItem = findViewById(R.id.etItem);
        etQuantidade = findViewById(R.id.etQuantidade);
        etValorUnitario = findViewById(R.id.etValorUnitario);
        etParcelas = findViewById(R.id.etParcelas);
        btnAdicionar = findViewById(R.id.btnAdicionar);
        btnConcluir = findViewById(R.id.btnConcluir);
        tvLista = findViewById(R.id.tvLista);
        tvTotal = findViewById(R.id.tvTotal);
        tvTotalItens = findViewById(R.id.tvTotalItens);
        tvParcelas = findViewById(R.id.tvParcelas);
        tvValorTotal = findViewById(R.id.tvValorTotal);
        spPagamento = findViewById(R.id.spPagamento);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pagamento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPagamento.setAdapter(adapter);

        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarItem();
            }
        });

        btnConcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                concluirPedido();
            }
        });

        spPagamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    etParcelas.setVisibility(View.VISIBLE);
                    tvParcelas.setVisibility(View.VISIBLE);
                } else {
                    etParcelas.setVisibility(View.GONE);
                    tvParcelas.setVisibility(View.GONE);
                }
                atualizarValorTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void adicionarItem() {
        if (validarCamposItem()) {
            String item = etItem.getText().toString();
            int quantidade = Integer.parseInt(etQuantidade.getText().toString());
            double valorUnitario = Double.parseDouble(etValorUnitario.getText().toString());
            double valorTotalItem = quantidade * valorUnitario;

            totalItens += quantidade;
            totalValor += valorTotalItem;

            pedidos.add(item + " - " + quantidade + " - R$ " + formatarValor(valorTotalItem));

            atualizarListaItens();
            atualizarTotal();
            limparCamposItem();
        }
    }

    private void atualizarListaItens() {
        StringBuilder listaItens = new StringBuilder();
        for (String pedido : pedidos) {
            listaItens.append(pedido).append("\n");
        }
        tvLista.setText(listaItens.toString());
    }

    private void atualizarTotal() {
        tvTotal.setText("Total: R$ " + formatarValor(totalValor));
        tvTotalItens.setText("Itens: " + totalItens);
    }

    private void atualizarValorTotal() {
        int pagamento = spPagamento.getSelectedItemPosition();
        double valorTotalFinal = totalValor;

        if (pagamento == 0) { // À vista
            valorTotalFinal *= 0.95;
        } else { // À prazo
            valorTotalFinal *= 1.05;
        }

        tvValorTotal.setText("Valor total: R$ " + formatarValor(valorTotalFinal));
    }

    private void concluirPedido() {
        if (validarCamposCliente()) {
            Toast.makeText(this, "Pedido " + pedidoId + " cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            limparCamposCliente();
            limparPedido();
        }
    }

    private boolean validarCamposItem() {
        if (TextUtils.isEmpty(etItem.getText()) || TextUtils.isEmpty(etQuantidade.getText()) || TextUtils.isEmpty(etValorUnitario.getText())) {
            Toast.makeText(this, "Preencha todos os campos do item!", Toast.LENGTH_SHORT).show();
            return false;
        }

        int quantidade = Integer.parseInt(etQuantidade.getText().toString());
        double valorUnitario = Double.parseDouble(etValorUnitario.getText().toString());

        if (quantidade <= 0 || valorUnitario <= 0) {
            Toast.makeText(this, "A quantidade e o valor unitário devem ser maiores que 0!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validarCamposCliente() {
        if (TextUtils.isEmpty(etNome.getText()) || TextUtils.isEmpty(etCpf.getText())) {
            Toast.makeText(this, "Preencha o nome e o CPF do cliente!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (pedidos.isEmpty()) {
            Toast.makeText(this, "Adicione pelo menos um item ao pedido!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spPagamento.getSelectedItemPosition() == 1 && TextUtils.isEmpty(etParcelas.getText())) {
            Toast.makeText(this, "Preencha a quantidade de parcelas!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void limparCamposItem() {
        etItem.getText().clear();
        etQuantidade.getText().clear();
        etValorUnitario.getText().clear();
    }

    private void limparCamposCliente() {
        etNome.getText().clear();
        etCpf.getText().clear();
    }

    private void limparPedido() {
        pedidos.clear();
        totalItens = 0;
        totalValor = 0;
        pedidoId++;
        atualizarListaItens();
        atualizarTotal();
        atualizarValorTotal();
    }

    private String formatarValor(double valor) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(valor);
    }
}
